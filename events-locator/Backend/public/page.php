<?php header('Content-Type: application/json');
require_once 'functions.php';

$id = $_REQUEST["id"];

$db = new DB_Functions();

$data = $db->dataByPageId($id);
if ($data != false) {
    echo json_encode($data);
} else {
	$response["error"] = 1;
	$response["error_msg"] = "Something went wrong!";
	echo json_encode($response);
}
mysql_close();