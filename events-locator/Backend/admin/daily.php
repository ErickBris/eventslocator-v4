<?php
require_once 'functions.php';
$db = new DB_Functions();

$data = $db->deleteEvents();
$db->close();

include 'fb-fetch.php';