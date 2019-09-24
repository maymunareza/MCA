<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array
 
//Check for Mandatory parameters
if(isset($input['search_string']))
{
	$search_string = $input['search_string'];
	$query    = "SELECT member_id, first_name, mid_name, last_name FROM members WHERE MATCH (member_id,first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, gender) AGAINST(? IN BOOLEAN MODE)";

	if($stmt = $con->prepare($query)){
		$stmt->bind_param("s",$search_string);
		$stmt->execute();
		$result = $stmt->get_result();

		$response["data"] = array();

		if($result->num_rows === 0)
		{
			$response["status"] = 1;
			$response["message"] = "No search results";
		}

		while($row = $result->fetch_assoc())
		{
			$data = array();
			$data["member_id"] = $row["member_id"];
			$data["first_name"] = $row["first_name"];
			$data["mid_name"] = $row["mid_name"];
			$data["last_name"] = $row["last_name"];

			array_push($response["data"], $data);
		}
		//$stmt->bind_result($member_id, $first_name, $mid_name, $last_name);
		
		//if($stmt->rowCount())
		//{
			//$row_all = $stmt->fetchall(PDO::FETCH_ASSOC);
			//echo json_encode($row_all);
		//}
		//else{
		//	$response["status"] = 1;
		//	$response["message"] = "Invalid username and password combination";
		//}
		
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
