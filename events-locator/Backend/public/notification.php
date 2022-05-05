<?php header('Content-Type: application/json');
require_once 'functions.php';

$db = new DB_Functions();

$data = $db->getNotification();
if ($data != false) {
    echo json_encode($data);
} else {
	$response["error"] = 1;
	$response["error_msg"] = "Something went wrong!";
	echo json_encode($response);
}
mysql_close();