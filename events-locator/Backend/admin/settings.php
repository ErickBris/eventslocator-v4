<?php 
    $nav = 1;
    include('header.php');

    if(file_exists('../config.php')) {
      include('../config.php');
    }
?>

    <div class="container">
        <form action="config-writer.php" method="post">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Settings</h3>
                </div>
                <div class="panel-body">

                    <div class="row">
                        <div class="col-md-6">
                            <h4>Database</h4>

                            <div class="form-group">
                                <label for="inputHost">Host</label>
                                <input name="host" type="text" class="form-control" id="inputHost" value="<?php if(defined('DB_HOST')) {echo DB_HOST;} else {echo 'localhost';} ?>">
                            </div>

                            <div class="form-group">
                                <label for="inputUsername">Username</label>
                                <input name="username" type="text" class="form-control" id="inputUsername" value="<?php if(defined('DB_USER')) {echo DB_USER;} ?>">
                            </div>

                            <div class="form-group">
                                <label for="inputPassword">Password</label>
                                <input name="password" type="password" class="form-control" id="inputPassword" value="<?php if(defined('DB_PASSWORD')) {echo DB_PASSWORD;} ?>">
                            </div>

                            <div class="form-group">
                                <label for="inputDatabase">Database</label>
                                <input name="database" type="text" class="form-control" id="inputDatabase" value="<?php if(defined('DB_DATABASE')) {echo DB_DATABASE;} ?>">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h4>Facebook app</h4>

                            <div class="form-group">
                                <label for="inputAppId">App ID</label>
                                <input name="appid" type="text" class="form-control" id="inputAppId" value="<?php if(defined('FB_APP_ID')) {echo FB_APP_ID;} ?>">
                            </div>

                            <div class="form-group">
                                <label for="inputAppSecret">App secret</label>
                                <input name="appsecret" type="text" class="form-control" id="inputAppSecret" value="<?php if(defined('FB_APP_SECRET')) {echo FB_APP_SECRET;} ?>">
                            </div>

                            <div class="form-group">
                                <label for="inputGraphVersion">Graph API version</label>
                                <input name="graphversion" type="text" class="form-control" id="inputGraphVersion" value="<?php if(defined('FB_GRAPH_VERSION')) {echo FB_GRAPH_VERSION;} else {echo 'v2.5';} ?>">
                            </div>


                        </div>
                    </div>

                </div>
                <div class="panel-footer clearfix">
                    <button type="submit" class="btn btn-default pull-right">
                        <?php if(defined('DB_HOST')) {echo "Update";} else {echo "Submit";} ?>
                    </button>
                </div>
            </div>
        </form>
    </div>
    <?php if(isset( $_GET['form'])) { $modal = ['title' => 'Response', 'content' => $_GET['form']]; include('modal-simple.php'); } ?>
        <?php include('footer.php') ?>
