<?php
    $menu = [['url' => 'index.php', 'name' => 'Home'], ['url' => 'settings.php', 'name' => 'Settings'], ['url' => 'docs.php', 'name' => 'Documentation']];
?>

    <!doctype html>
    <html>

    <head>
        <meta charset="utf8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>EventsLocator v4</title>
        <link rel="stylesheet" href="bootstrap.min.css" async>
        <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" async>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <style>
            .tab-content {
                margin-top: 20px;
            }
        </style>
    </head>

    <body>
        <nav class="navbar navbar-default navbar-static-top">
            <div class="container-fluid">

                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">EventsLocator4</a>
                </div>

                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav navbar-right">
                        <?php
                            $i = 0;
                            foreach($menu as $item) {
                                $li = "<li";
                                $liend = "</a></li>";
                                if($nav === $i) {
                                    $li .= " class='active'";
                                    $liend = " <span class='sr-only'>(current)</span></a></li>";
                                }
                                $li .= "><a href='". $item['url'] ."'>" . $item['name'] . $liend;
                                echo $li;
                                $i++;
                            }
                        ?>
                    </ul>
                </div>

            </div>
        </nav>
