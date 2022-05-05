<?php 
    $nav = 0;
    include('header.php');

    require_once 'functions.php';
    $db = new DB_Functions(); 
    


    $offset = 0;
    $limit = 10;
    if(isset( $_GET['offset'])){
        $offset = $_GET['offset'];
    }

    $data = $db->getPages($offset, $limit);
    if($data['success']) {
        $pages = $data['data'];
    }
    $db->close();
    ?>

    <div class="container-fluid">
        <div class="panel panel-default">
            <div class="panel-body clearfix">
                <div class="form-inline">
                    <form action="add.php" method="post">
                        <div class="form-group">
                            <label class="sr-only" for="inputPageID">Facebook Page ID</label>
                            <input name="id" type="text" class="form-control" id="inputPageID" placeholder="Facebook Page ID">
                        </div>
                        <button type="submit" class="btn btn-default">Add</button>
                        <?php echo $_GET['form'] ?>
                            <a href="fb-fetch.php" target="_blank" class="btn btn-default pull-right">Fetch events</a>
                    </form>
                </div>

            </div>
        </div>

        <nav>
            <ul class="pager">
                <?php 
                        if($offset > 0) {
                            $prev = $offset - $limit;
                            if($prev<0) {
                                $prev = 0;
                            }
                            echo "<li class='previous'><a href='index.php?offset=".$prev."'><span aria-hidden='true'>&larr;</span> Previous</a></li>";
                        }
                        if(count($pages)===$limit) {
                            echo "<li class='next'><a href='index.php?offset=".($offset + $limit)."'>Next <span aria-hidden='true'>&rarr;</span></a></li>";
                        }
                    ?>
            </ul>
        </nav>

        <div class="panel panel-default">
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Street</th>
                                <th>City</th>
                                <th>Zip</th>
                                <th>Country</th>
                                <th>Latitude</th>
                                <th>Longitude</th>
                                <th>Category</th>
                                <th>Photo</th>
                                <th>Cover</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="pages-table">
                            <?php
                        $counter = $offset;
                        foreach($pages as $row) {
                            echo "<tr>
                                    <td>".(1+$counter)."</td>
                                    <td>".$row['pageId']."</td>
                                    <td>".$row['name']."</td>
                                    <td>".$row['street']."</td>
                                    <td>".$row['city']."</td>
                                    <td>".$row['zip']."</td>
                                    <td>".$row['country']."</td>
                                    <td>".$row['latitude']."</td>
                                    <td>".$row['longitude']."</td>
                                    <td>".$row['category']."</td>
                                    <td><a href='".$row['photo']."' target='_blank'>link</a></td>
                                    <td><a href='".$row['coverPhoto']."' target='_blank'>link</a></td>
                                    <td><a href='remove.php?id=".$row['pageId']."'><i class='material-icons'>delete</i></a></td>
                                </tr>";
                            $counter++;
                        }
                    ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <?php include('footer.php') ?>
