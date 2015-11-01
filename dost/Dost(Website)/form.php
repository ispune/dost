<?php

	$email = $_POST['email'];
	$name = $_POST['name'];
	$number = $_POST['number'];
	$title = $_POST['Title'];
	$question = $_POST['question'];
	
	
	if(strlen($_POST['number']) !== 10) {
      //Here is not 11 characters long
		exit("Please provide 10 character number");   
   }

	$user_update = mysqli_connect("localhost", "root", "") or die ("Error Occured");
	mysqli_select_db($user_update,"dost");

    
    //$sql="SELECT Lastname,Age FROM Persons ORDER BY Lastname";
	//$result = $user_update->query("SELECT FROM `user`(`email`, `name`, `number`, `sms`, `expert`, `exp_field`) WHERE name=$name");
    $sql="SELECT u_id FROM `user` WHERE number='$number'";
    $result=mysqli_query($user_update,$sql);
    $rowcount=mysqli_num_rows($result);
 	if($rowcount>=1)
   	{ 
   		echo "User Already Exists";
    	$sql2 =	"INSERT INTO `question`(`title`, `description`) VALUES ('$title','$question')";
    	mysqli_query($user_update, $sql2) or die("Something Happened");
    	$last_id = $user_update->insert_id;
    	echo "Question Added";

    	$row = mysqli_fetch_array($result,MYSQLI_NUM);
    	$sql_statement = "INSERT INTO `user_question`(`u_id`, `q_id`) VALUES ('$row[0]','$last_id')";
    	mysqli_query($user_update, $sql_statement) or die("Something Happened");
		
		if (isset($_POST['test'])){
			$sql3 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('1','$last_id')";
			mysqli_query($user_update, $sql3) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option2'])){
			$sql4 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('2','$last_id')";
			mysqli_query($user_update, $sql4) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option3'])){
			$sql5 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('3','$last_id')";
			mysqli_query($user_update, $sql5) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option4'])){
			$sql6 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('4','$last_id')";
			mysqli_query($user_update, $sql6) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option5'])){
			$sql6 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('4','$last_id')";
			mysqli_query($user_update, $sql6) or die("Something Happened here");
			echo "$last_id";
		}
   	}
 	else
    {
    	
   		$sql1 = "INSERT INTO `user`(`email`, `name`, `number`, `sms`, `expert`, `exp_field`) VALUES ('$email','$name','$number','1','0','0')";
   		$sql2 =	"INSERT INTO `question`(`title`, `description`) VALUES ('$title','$question')";
		mysqli_query($user_update, $sql1) or die("Something Happened");
		echo "User Added";
		$last_id1 = $user_update->insert_id;
		mysqli_query($user_update, $sql2) or die("Something Happened");
		echo "Question Added";
		$last_id = $user_update->insert_id;

    	$sql_statement = "INSERT INTO `user_question`(`u_id`, `q_id`) VALUES ('$last_id1','$last_id')";
    	mysqli_query($user_update, $sql_statement) or die("Something Happened");
		
		if (isset($_POST['test'])){
			$sql3 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('1','$last_id')";
			mysqli_query($user_update, $sql3) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option2'])){
			$sql4 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('2','$last_id')";
			mysqli_query($user_update, $sql4) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option3'])){
			$sql5 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('3','$last_id')";
			mysqli_query($user_update, $sql5) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option4'])){
			$sql6 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('4','$last_id')";
			mysqli_query($user_update, $sql6) or die("Something Happened here");
			echo "$last_id";
		}
		if (isset($_POST['option5'])){
			$sql6 =	"INSERT INTO `belongs`(`t_id`, `q_id`) VALUES ('4','$last_id')";
			mysqli_query($user_update, $sql6) or die("Something Happened here");
			echo "$last_id";
		}
    }

	mysqli_close($user_update);


?>