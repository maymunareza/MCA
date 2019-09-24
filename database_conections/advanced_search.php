<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array

$city_filter = false;
$zip_filter = false;
$string_filter = false;
 
//Check for Mandatory parameters
if(isset($input['search_string']))
{
	$search_string = $input['search_string'];

	$first_filter = true;

	$by_year = $input['by_year'];
	$by_city = $input['by_city'];
	$by_zip = $input['by_zip'];

	$query    = "SELECT member_id, first_name, mid_name, last_name FROM members";

	if ($by_city != "") {
		if ($first_filter){
			$query .= " WHERE city = ?";
			$first_filter = false;
		}
		else {
			$query .= " AND city = ?";
		}
		$city_filter = true;
	}

	if ($by_zip != "") {
		if ($first_filter){
			$query .= " WHERE zip_code = ?";
			$first_filter = false;
		}
		else {
			$query .= " AND zip_code = ?";
		}
		$zip_filter = true;
	}

	if ($search_string != "") {
		if ($first_filter){
			$query .= " WHERE MATCH (member_id,first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, gender) AGAINST(? IN BOOLEAN MODE)";
			$first_filter = false;
		}
		else {
			$query .= " AND MATCH (member_id,first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, gender) AGAINST(? IN BOOLEAN MODE)";
		}
		$string_filter = true;
	}


	if($stmt = $con->prepare($query)){
		
		// bind parameters
		// 111
		if ($city_filter && $zip_filter && $string_filter) {
			$stmt->bind_param("sss", $by_city, $by_zip, $search_string);
		}
		// 110
		elseif ($city_filter && $zip_filter && !$string_filter) {
			$stmt->bind_param("ss", $by_city, $by_zip);
		}
		// 101
		elseif ($city_filter && !$zip_filter && $string_filter) {
			$stmt->bind_param("ss", $by_city, $search_string);
		}
		// 100
		elseif ($city_filter && !$zip_filter && !$string_filter) {
			$stmt->bind_param("s", $by_city);
		} 
		//011
		elseif (!$city_filter && $zip_filter && $string_filter) {
			$stmt->bind_param("ss", $by_zip, $search_string);
		}
		//010
		elseif (!$city_filter && $zip_filter && !$string_filter) {
			$stmt->bind_param("s", $by_zip);
		}
		//001
		elseif (!$city_filter && !$zip_filter && $string_filter) {
			$stmt->bind_param("s", $search_string);
		}
		//000: ha ha should never get to this point so
		
		/*else
		{
			//undef behavior lmao
			//$stmt->bind_param("s",$search_string);
			//TODO: go back and make sure this NEVER happens.
			// lmao wait update means it might happen oops OPPS
		}*/


		//$stmt->bind_param("s",$search_string);
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

			$member_id = $data["member_id"];

			// filter by year by checking registration table 
			if ($by_year != "")
			{
				$year_search_query = "SELECT member_id FROM member_registration WHERE member_id = ? AND hijri_year = ?";

				if($year_stmt = $con->prepare($year_search_query)){
					$year_stmt->bind_param("ss", $member_id, $by_year);
					$year_stmt->execute();
					$year_result = $year_stmt->get_result();

					if($year_result->num_rows != 0)
					{
						array_push($response["data"], $data);
					}
					
					$year_stmt->close();
				}

			}
			else
			{
				array_push($response["data"], $data);
			}
			
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
