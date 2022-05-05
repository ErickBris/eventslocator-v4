<?php

$newfile = fopen("../config.php", "w") or die("Unable to write to config.php!");

$config = "<?php
define('DB_HOST', '". $_POST['host'] ."');
define('DB_USER', '". $_POST['username'] ."');
define('DB_PASSWORD', '". $_POST['password'] ."');
define('DB_DATABASE', '". $_POST['database'] ."');
define('FB_APP_ID', '". $_POST['appid'] ."');
define('FB_APP_SECRET', '". $_POST['appsecret'] ."');
define('FB_GRAPH_VERSION', '". $_POST['graphversion'] ."');";

fwrite($newfile, $config);
fclose($newfile);

header("Location: settings.php?form=Success");
die();