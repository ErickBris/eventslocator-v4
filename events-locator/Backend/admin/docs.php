<?php $nav = 2; include('header.php');?>

    <div class="container-fluid">
        <div class="row">
            <div class="col-md-2 col-sm-12">
                <ul class="nav nav-pills nav-stacked" role="tablist">
                    <li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="pill">Home</a></li>
                    <li role="presentation"><a href="#get-started" aria-controls="get-started" role="tab" data-toggle="pill">Get started</a></li>
                    <li role="presentation"><a href="#server" aria-controls="server" role="tab" data-toggle="pill">Setting up the database</a></li>
                    <li role="presentation"><a href="#app-setup" aria-controls="app-setup" role="tab" data-toggle="pill">Setting up the app</a></li>
                </ul>
            </div>
            <div class="col-md-10 col-sm-12">
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="home">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Release info</h3>
                            </div>
                            <div class="panel-body">
                                <p>
                                    <b>Author:</b> Aleš Kovačič (<a href="mailto:aleskovacic@mail.com" target="_top">aleskovacic@mail.com</a>)
                                    <br>
                                    <b>First release:</b> 20.05.2014
                                    <br>
                                    <b>Latest update:</b> 23.10.2015
                                    <br>
                                    <b>Version:</b> 4.0
                                    <br>
                                    <b>Changes:</b>
                                    <br> - Admin panel for database management,
                                    <br> - security fixes,
                                    <br> - Facebook SDK version update,
                                    <br> - redesigned android app,
                                    <br> - removed notifications feature,
                                    <br> - updated dependencies,
                                    <br> - showing events on the map instead of Facebook pages,
                                    <br> - removed "add to calendar" and "navigation" buttons,
                                    <br> - removed PageActivity.
                                </p>
                                <p>Note: features that were removed will be improved and added back in the next release (v4.1).</p>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="get-started">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Get started</h3>
                            </div>
                            <div class="panel-body">
                                <p>
                                    EventsLocator is a full android java application with PHP backend. You'll need Android Studio with Android SDK installed on your computer so that you can import and edit the app. Also you'll need a server with MySQL and PHP (5.5 and up) support. Please follow these instructions carefully.
                                </p>
                                <p>If you get stuck don't hesitate to contact me. I recommend setting up the database first.</p>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="server">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Setting up the database</h3>
                            </div>
                            <div class="panel-body">
                                <p>
                                    For easier and faster setup you'll find the .sql file in /database folder. Import it in your MySQL database.
                                </p>
                                <p>
                                    For a tutorial on creating the database and importing tables in cPanel click <a href="https://www.youtube.com/watch?v=MlCE47X81Fs" target="_blank">here</a>.
                                </p>

                                <h4>Setting up admin panel</h4>
                                <p>
                                    Upload contents inside /backend folder to your server. Chmod /public folder to make it accessible to everyone. On the other hand you should secure /admin folder.
                                </p>
                                <p>
                                    In your browser navigate to /admin/settings.php, enter your database and Facebook app info and save it.
                                </p>
                                <h4>Populating database</h4>
                                <p>
                                    Navigate to 'Pages' in your admin panel and add a Facebook page by typing in it's ID. You should see all the info fetched from Facebook in the table below. You should be able to fetch events now.
                                </p>
                                <h4>CRON jobs</h4>
                                <p>
                                    daily.php and fb-fetch.php are scripts written for maintaining your database in good health. They should be executed at least once per day. Their function is:
                                    <br> - daily.php truncates the whole Event table and executes fb-fetch.php,
                                    <br> - fb-fetch.php gets all pages from the database and fetches upcoming events for those pages. If events already exist in the database, it updates them.
                                </p>
                            </div>
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="app-setup">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Setting up the app</h3>
                            </div>
                            <div class="panel-body">
                                <p>
                                    You'll need Android Studio with the latest SDK. You can download it from <a href="http://developer.android.com/sdk/index.html" target="_blank">here</a>.
                                </p>

                                <h4>Import the project</h4>
                                <p>
                                    Open Android Studio and select File -> Import Project. Navigate to the EventsLocator4 project folder and click OK to import.
                                </p>

                                <h4>Enable Google Maps API</h4>
                                <p>
                                    Follow <a href="https://developers.google.com/maps/documentation/android-api/signup" target="_blank">this</a> tutorial to get your API key. Then insert it inside strings.xml resource file (string name 'maps_api_key').
                                </p>

                                <h4>Enable AdMob</h4>
                                <p>
                                    After creating a new banner ad, place its banner_ad_unit_id inside strings.xml resource file (string name 'banner_ad_unit_id').
                                </p>

                                <h4>Connect with your server</h4>
                                <p>
                                    Under java resource files you'll find App.java where replace 'SERVER_URL' with the one that points to your /public directory.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <?php include('footer.php');?>