<?php
setlocale (LC_ALL, 'utf-8');
date_default_timezone_set('UTC');

if(file_exists('../config.php')) {
	require_once '../config.php';
}

class DB_Functions {
    private $db;
	private $fb;

    function __construct() {
		$this->db = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
		$this->db->set_charset("utf8");
    }
	
	public function checkConnection() {
		if($this->db->connect_errno) {
			return ['canConnect' => false, 'success' => false, 'response' => mysqli_connect_error()];
		}
		if($this->db->error) {
			return ['canConnect' => true, 'success' => false, 'response' => $this->db->error];
		}
		return ['canConnect' => true, 'success' => true];
	}
	
	public function close() {
		$this->db->close();
	}
	
	public function getFb() {
		require_once '../vendor/autoload.php';
		$this->fb = new Facebook\Facebook([
  			'app_id' => FB_APP_ID,
  			'app_secret' => FB_APP_SECRET,
  			'default_graph_version' => FB_GRAPH_VERSION
  		]);
		$this->fb->setDefaultAccessToken($this->fb->getApp()->getAccessToken());
		return $this->fb;
	}
	
	public function fetchAll($time) {
		$select = $this->db->prepare("SELECT Event.EventID, Event.PageID, Event.PlaceID, Event.Title, 
		Event.StartTime, Event.EndTime, Event.Description, Event.CoverPhoto, Event.AttendeesCount, 
		Place.Name, Place.Street, Place.City, Place.Zip, Place.Country, Place.Latitude, Place.Longitude, Page.Name
		FROM Event 
		INNER JOIN Place ON Place.PlaceID = Event.PlaceID 
		INNER JOIN Page ON Page.PageID = Event.PageID 
		WHERE (Event.EndTime >= CURDATE()
		OR Event.StartTime >= CURDATE())
		AND Event.StartTime <= DATE_ADD(CURDATE(), INTERVAL ? DAY)
		GROUP BY Event.EventID");
		$select->bind_param('i', $time);
		$select->execute();
		$select->bind_result($eventId, $pageId, $placeId, $title, $startTime, $endTime, 
		$description, $coverPhoto, $attendeesCount, $placeName, $street, $city, $zip, $country, $latitude, $longitude, $pageName);
		$array = [];
		
		while($select->fetch()) {
			array_push($array, array(
				'eventId' => (int)$eventId,
				'pageId' => (int)$pageId,
				'placeId' => (int)$placeId,
				'title' => $title,
				'startTime' => $startTime,
				'endTime' => $endTime,
				'description' => $description,
				'coverPhoto' => $coverPhoto,
				'attendeesCount' => (int)$attendeesCount,
				'placeName' => $placeName,
				'street' => $street,
				'city' => $city,
				'zip' => $zip,
				'country' => $country,
				'latitude' => (double)$latitude,
				'longitude' => (double)$longitude,
				'pageName' => $pageName));
		}
		
		if($select->error){
			return ['success'=>false, 'error'=> $select->error];
		}
		
		return ['success'=>true, 'data'=>$array];
    }
    
    public function getPagesRef() {
		$select = $this->db->query("SELECT PageID FROM Page");
		$array = [];
		
		while($row = $select->fetch_array(MYSQLI_ASSOC)) {
			array_push($array, array(
				'pageid' => $row['PageID']));
		}
		
		if($select->error){
			return ['success'=>false, 'response'=>['response' => 'error', 'message' => $select->error]];
		}
		
		return ['success'=>true, 'response'=>$array];
    }
    
    public function getPages($offset, $limit) {
		$select = $this->db->prepare("SELECT PageID, Created, Name, Street, City, Zip, Country, Latitude, Longitude, Category, Photo, CoverPhoto 
        FROM Page ORDER BY Created DESC LIMIT ?, ?");
        $select->bind_param('ii', $offset, $limit);
		$select->execute();
        $select->bind_result($pageId, $created, $name, $street, $city, $zip, $country, $latitude, $longitude, $category, $photo, $coverPhoto);
		$array = [];
		
		while($select->fetch()) {
			array_push($array, array(
				'pageId' => (int)$pageId,
				'name' => $name,
				'street' => $street,
				'city' => $city,
				'zip' => $zip,
				'country' => $country,
				'latitude' => (double)$latitude,
				'longitude' => (double)$longitude,
				'category' => $category,
				'photo' => $photo,
				'coverPhoto' => $coverPhoto,
            ));
		}
		
		if($select->error){
			return ['success'=>false, 'error'=> $select->error];
		}
		
		return ['success'=>true, 'data'=>$array];
    }

	public function getNotification() {
		$res = mysql_query("SELECT * FROM notification ORDER BY id DESC LIMIT 1") or die(mysql_error());

		$row = mysql_fetch_array($res, MYSQL_ASSOC) ;

		return $row;
	}
	
	public function createPlace($placeID, $name, $street, $city, $zip, $country, $latitude, $longitude) {

		$select = $this->db->prepare("SELECT * FROM Place WHERE PlaceID = ? LIMIT 1");
		$select->bind_param('i', $placeID);
		$select->execute();
		if($select->fetch()) {
			$select->free_result();
			//update
			$update = $this->db->prepare("UPDATE Place SET Name = ?, Street = ?, City = ?, Zip = ?, Country = ?, Latitude = ?, Longitude = ? WHERE PlaceID = ?");
			$update->bind_param('sssssddi', $name, $street, $city, $zip, $country, $latitude, $longitude, $placeID);
			if($update->execute()){return ['success' => true, 'response' => ['response' => 'success', 'message' => 'Entry successfully updated']];};
			return ['success' => false, 'response' => ['response' => 'error', 'message' => $update->error]];
		}
		//create new
		$insert = $this->db->prepare("INSERT INTO Place (PlaceID, Name, Street, City, Zip, Country, Latitude, Longitude) VALUES (?,?,?,?,?,?,?,?)");
		$insert->bind_param('isssssdd', $placeID, $name, $street, $city, $zip, $country, $latitude, $longitude);
		if($insert->execute()){return ['success' => true, 'response' => ['response' => 'success', 'message' => 'Entry successfully inserted']];};
		return ['success' => false, 'response' => ['response' => 'error', 'message' => $insert->error]];
	}
	
	public function createEvent($eventId,$pageId,$placeId,$title,$start,$end,$description,$picture,$attending) {
		$select = $this->db->prepare("SELECT * FROM Event WHERE EventID = ? LIMIT 1");
		$select->bind_param('i', $eventId);
		$select->execute();
		if($select->fetch()) {
			$select->free_result();
			//update
			$update = $this->db->prepare("UPDATE Event SET PlaceID = ?, Title = ?, StartTime = ?, EndTime = ?, Description = ?, CoverPhoto = ?, AttendeesCount = ? WHERE EventID = ?");
			$update->bind_param('isssssii', $placeId,$title,$start,$end,$description,$picture,$attending,$eventId);
			if($update->execute()){return ['success' => true, 'response' => ['response' => 'success', 'message' => 'Entry successfully updated']];};
			return ['success' => false, 'response' => ['response' => 'error', 'message' => $update->error]];
		} else {
			//create new
			if(!$insert = $this->db->prepare("INSERT INTO Event (EventID, PageID, PlaceID, Title, StartTime, EndTime, Description, CoverPhoto, AttendeesCount) VALUES (?,?,?,?,?,?,?,?,?)")) {
				echo "Prepare failed: (" . $this->db->errno . ") " . $this->db->error;
			}
			$insert->bind_param('iiisssssi', $eventId,$pageId,$placeId,$title,$start,$end,$description,$picture,$attending);
			if($insert->execute()){return ['success' => true, 'response' => ['response' => 'success', 'message' => 'Entry successfully inserted']];};
			return ['success' => false, 'response' => ['response' => 'error', 'message' => $insert->error]];
		}
	}
	
	public function addPage($pageId,$name,$street,$city,$zip,$country,$lat,$lon,$category,$picture,$cover) {
		$select = $this->db->prepare("SELECT PageID FROM Page WHERE PageID = ? LIMIT 1");
		$select->bind_param('i', $pageId);
		$select->execute();
		if($select->fetch()) {
			return ['response' => 'failed', 'message' => 'Entry already exists in the database'];
		} 
		
		$insert = $this->db->prepare("INSERT INTO Page (PageID, Name, Street, City, Zip, Country, Latitude, Longitude, Category, Photo, CoverPhoto) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		$insert->bind_param('isssssddsss', $pageId,$name,$street,$city,$zip,$country,$lat,$lon,$category,$picture,$cover);
		if($insert->execute()) {
			return ['response' => 'success', 'message' => 'Entry successfully inserted into the database'];
		}

		return ['response' => 'error', 'message' => $insert->error];
	}
	
	public function removePage($pageId) {
		$select = $this->db->prepare("DELETE FROM Page WHERE PageID = ?");
		$select->bind_param('i', $pageId);
		$select->execute();

		return true;
	}
	
	public function deleteEvents() {
		$truncate = $this->db->prepare("TRUNCATE TABLE Event");
		if($truncate->execute()) {
			return ['response' => 'success', 'message' => 'Table successfully truncated'];
		}
		return ['response' => 'error', 'message' => $truncate->error];
    }
}