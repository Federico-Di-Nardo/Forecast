<?php

include 'connection.php';


//$mysqli = $connection;
//$mysqli->query("CALL `returnHighestAlarm`(@p0)");
//$resultado = $mysqli->query("SELECT @p0 as highestAlarm");
//$fila = $resultado->fetch_assoc();
//echo (json_encode($fila));
//echo ($fila[])


$con = $connection;
    if (!$con) {
        die('Could not connect: ' . mysqli_error($con));
    }

    mysqli_select_db($con,"forecast");
    
	
	$con->query("CALL `returnHighestAlarm`(@p0)");
	$resultado = $con->query("SELECT @p0 as highestAlarm");
    $result = mysqli_fetch_assoc($resultado);
    $resultstring = $result['highestAlarm'];
	
	$resultado2 = $con->query("CALL `returnAlarmData`($resultstring)");
	//$result2 = mysqli_fetch_assoc($resultado2);
	
	
	
	
	while ($result2 = mysqli_fetch_assoc($resultado2)) {
		if ($result2['age'] <31 ){
		$result2['age'] = 1;
	}
	if ($result2['age'] <61 && $result2['age'] > 30){
		$result2['age'] = 2;
	}
	if ($result2['age'] >60){
		$result2['age'] = 3;
	}
	$resultstring2 = $result2['GenderID'] .",". $result2['age'] .",". $result2['TimeSlotID'] .",". $result2['alarms.AmountOfAlarms/highestAlarm']."+";
	

    echo $resultstring2;
	
	}
echo "*";
	
	
    

    mysqli_close($con);
	
?>
