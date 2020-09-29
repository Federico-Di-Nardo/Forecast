<?php

include 'connection.php';

$newAlarms=$_GET["newAlarms"];

$query = "CALL `updateLastTraining`('$newAlarms')";

mysqli_query($connection,$query) or die (mysqli_error());
mysqli_close($connection);
echo "base de datos actualizada";

?>