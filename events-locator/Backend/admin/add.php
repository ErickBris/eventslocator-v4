<?php header('Content-Type: application/json');
require_once 'functions.php';

$id = $_POST['id'];
if(isset($id)) {
	$db = new DB_Functions();
	$fb = $db->getFb();
	
	try {
		$response = $fb->get('/' . $id . '?fields=id,name,category,cover,location,picture.type(large)');
		$pageNode = $response->getGraphPage();
	} catch(Facebook\Exceptions\FacebookResponseException $e) {
		echo json_encode($e->getResponseData());
		exit;
	} catch(Facebook\Exceptions\FacebookSDKException $e) {
		echo json_encode($e->getResponseData());
		exit;
	}

	$location = $pageNode->getLocation();
	if($location != null){
		$latitude = $location->getLatitude();
		$longitude = $location->getLongitude();
		$street = $location->getStreet();
		$city = $location->getCity();
		$zip = $location->getZip();
		$country = $location->getCountry();
	}

	if($pageNode->getField('picture')!=null) {
		$picture = $pageNode->getField('picture')->getField('url');
	}
	
	if($pageNode->getField('cover')!=null) {
		$cover = $pageNode->getField('cover')->getField('source');
	}
	
	$res = $db->addPage($pageNode->getId(), $pageNode->getName(), $street, $city, $zip, $country, $latitude, $longitude, $pageNode->getCategory(), $picture, $cover);
	$db->close();
    
    header("Location: index.php?form=" . $res['message']);
    die();
}
