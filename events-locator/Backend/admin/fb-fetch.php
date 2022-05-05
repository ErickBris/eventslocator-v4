<?php header('Content-Type: application/json');
require_once 'functions.php';

$db = new DB_Functions();
$fb = $db->getFb();
$pages = $db->getPagesRef();
if(!$pages['success']) {
	echo json_encode($pages['response']);
	exit;
}

$today = new DateTime();
$UTC = new DateTimeZone("UTC");

foreach ($pages['response'] as &$page){
	set_time_limit(60);
	try {
		$response = $fb->get('/' . $page['pageid'] . '/events?fields=owner,cover,attending_count,place,name,description,start_time,end_time');
		$graphEdge = $response->getGraphEdge('GraphEvent');
	} catch(Facebook\Exceptions\FacebookResponseException $e) {
		echo json_encode($e->getResponseData() + $page);
		exit;
	} catch(Facebook\Exceptions\FacebookSDKException $e) {
		echo json_encode($e->getResponseData() + $page);
		exit;
	}
		
	loopThroughEvents($graphEdge, $page);
}
echo json_encode(['response' => 'success']);
$db->close();


function loopThroughEvents($graphEdge, $page) {
    
	global $db, $fb, $today, $UTC;
	foreach ($graphEdge as $eventNode){
		$date = $eventNode->getStartTime();
		$date->setTimezone($UTC);
		$startDate = $date->format('Y-m-d H:i');
		
		if($date >= $today){
			$et = $eventNode->getEndTime();
			if($et!=null) {
				$et->setTimezone($UTC);
				$endDate = $et->format('Y-m-d H:i');
			}
			$place = $eventNode->getPlace();
			if($place!=null) {
				$placeID = $place->getId();
				$location = $place->getLocation();
				if($location == null) {continue;};
				$cP = $db->createPlace($placeID, $place->getName(), $location->getStreet(), $location->getCity(), $location->getZip(), $location->getCountry(), $location->getLatitude(), $location->getLongitude());
				if(!$cP['success']) {
					echo json_encode($cP['response'] + ['placeid' => $placeID, 'eventid' => $eventNode->getId()]);
					exit;
				}
			} else {
				continue;
			}
			if($eventNode->getCover()!=null) {
				$coverPhoto = $eventNode->getCover()->getSource();
			}
			
			$cE = $db->createEvent($eventNode->getId(),$page['pageid'],$placeID,$eventNode->getName(),$startDate,$endDate,$eventNode->getDescription(),$coverPhoto,$eventNode->getAttendingCount());
			if(!$cE['success']) {
				echo json_encode($cE['response']);
				exit;
			}
		} else {
			return;
		}
	}
    $nextpage = $fb->next($graphEdge);
    if($nextpage!=null) {
        loopThroughEvents($nextpage, $page);
    } else {
        return;
    }
}
