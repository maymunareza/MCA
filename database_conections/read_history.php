<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array

$todayDate = date("Y-m-d H:i:s");
 
//Check for Mandatory parameters
if(isset($input['member_id']))
{
	$member_id = $input['member_id'];
	$query    = "SELECT hijri_year, membership_date, prepaid FROM member_registration WHERE member_id = ?";

	if($stmt = $con->prepare($query)){
		$stmt->bind_param("s",$member_id);
		$stmt->execute();
		$result = $stmt->get_result();

		$response["data"] = array();

		if($result->num_rows === 0)
		{
			$response["status"] = 1;
			$response["message"] = "No history available";
		}
		else
		{
			$response["status"] = 0;
			$response["message"] = "Fetching history";
		}

		while($row = $result->fetch_assoc())
		{
			$data = array();
			$data["hijri_year"] = $row["hijri_year"];
			$data["membership_date"] = $row["membership_date"];
			$data["prepaid"] = $row["prepaid"];

			if ($data["membership_date"] < $todayDate)
			{
				array_push($response["data"], $data);
			}
		}
		
		$stmt->close();
	}
}
else{
	$response["status"] = 2;
	$response["message"] = "Missing mandatory parameters";
}
//Display the JSON response
echo json_encode($response);
?>
