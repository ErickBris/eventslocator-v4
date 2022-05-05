<?php
setlocale (LC_ALL, 'utf-8');
date_default_timezone_set('UTC');

if(file_exists('../config.php')) {
	require_once '../config.php';
}

class DB_Functions {
    private $db;

    function __construct() {
		$this->db = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
		if($this->db->connect_errno) { echo mysqli_connect_error(); exit();};
		if(!$this->db->set_charset("utf8")) { echo $this->db->error; exit();};
    }
	
	public function close() {
		$this->db->close();
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
		GROUP BY Event.EventID
		ORDER BY Event.StartTime ASC");
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

	public function getNotification() {
		$res = mysql_query("SELECT * FROM notification ORDER BY id DESC LIMIT 1") or die(mysql_error());

		$row = mysql_fetch_array($res, MYSQL_ASSOC) ;

		return $row;
	}
	
	
	public function search($squery) {
		$select = $this->db->prepare("SELECT Event.EventID, Event.PageID, Event.PlaceID, Event.Title, 
		Event.StartTime, Event.EndTime, Event.Description, Event.CoverPhoto, Event.AttendeesCount, 
		Place.Name, Place.Street, Place.City, Place.Zip, Place.Country, Place.Latitude, Place.Longitude, Page.Name
		FROM Event 
		INNER JOIN Place ON Place.PlaceID = Event.PlaceID 
		INNER JOIN Page ON Page.PageID = Event.PageID 
		WHERE Event.Title LIKE ?
		OR Page.Name LIKE ?
		OR Place.Name LIKE ?
		OR Place.Street LIKE ?
		OR Place.City LIKE ?
		OR Place.Country LIKE ?
		GROUP BY Event.EventID
		ORDER BY Event.StartTime ASC");
		$select->bind_param('ssssss', $squery, $squery, $squery, $squery, $squery, $squery);
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
	
	public function dataByPageId($id) {
		$select = $this->db->prepare("SELECT * FROM event WHERE event.pageid = ? AND start >= DATE(NOW()) ORDER BY start ASC");
		$select->bind_param('i', $id);
		$select->execute();
		if($array = $select->fetch()) {
			return $array;
		}	
			
		/*
		
		// or probably this way -
		
		while( $row = mysql_fetch_array($res) ) {
			array_push($array, array(
				'id' => $row['id'],
				'eventId' => $row['eventid'],
				'pageId' => $row['pageid'],
				'title' => $row['title'],
				'start' => $row['start'],
				'description' => $row['description'],
				'cover' => $row['image'],
				'attending' => $row['attending']));
		}*/
				
		return ['response' => 'error', 'message' => $select->error];;
    }
}
