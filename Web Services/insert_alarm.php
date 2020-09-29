<?php

include 'connection.php';

$driverID=$_POST["driverID"];
$timeSlot=$_POST["timeSlot"];
$date=$_POST["date"];
$amountOfAlarms=$_POST["amountOfAlarms"];


$query = "CALL addAlarm ('$driverID','$timeSlot','$date','$amountOfAlarms')";


mysqli_query($connection,$query) or die (mysqli_error());
mysqli_close($connection);
echo "base de datos actualizada con" . $driverID . $timeSlot . $date . $amountOfAlarms;

?>