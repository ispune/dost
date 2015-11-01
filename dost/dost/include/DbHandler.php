<?php

/**
 * Class to handle all db operations
 * This class will have CRUD methods for database tables
 *
 * @author Ravi Tamada
 * @link URL Tutorial link
 */
class DbHandler {

    private $conn;

    function __construct() {
        require_once dirname(__FILE__) . '/DbConnect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }

    /* ------------- `users` table method ------------------ */

    /**
     * Creating new user
     * @param String $name User full name
     * @param String $email User login email id
     * @param String $password User login password
     */
    public function createUser($name, $email,$pic_url,$contact, $sms) {
        require_once 'PassHash.php';
        $response = array();
        $email = urldecode($email);
        // First check if user already existed in db
        if (!$this->isUserExists($email)) {
            // Generating password hash
            //$password_hash = PassHash::hash($password);

            // Generating API key
            $api_key = $this->generateApiKey();
            
            //$refferal_code = 'ASHU10';
            $points = 0;
            $user_id = '';

            // insert query
            $stmt = $this->conn->prepare("INSERT INTO user(email, name, pic_url, number, sms, api_key) values(?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("ssssss", $email, $name, $pic_url, $contact, $sms, $api_key);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_CREATE_FAILED;
            }
        } else {
            // User with same email already existed in the db
            return USER_ALREADY_EXISTED;
        }

        return $response;
    }

    /**
     * Checking user login
     * @param String $email User login email id
     * @param String $password User login password
     * @return boolean User login status success/fail
     */
    public function checkLogin($email, $password) {
        // fetching user by email
        $stmt = $this->conn->prepare("SELECT password_hash FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->bind_result($password_hash);

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // Found user with the email
            // Now verify the password

            $stmt->fetch();

            $stmt->close();

            if (PassHash::check_password($password_hash, $password)) {
                // User password is correct
                return TRUE;
            } else {
                // user password is incorrect
                return FALSE;
            }
        } else {
            $stmt->close();

            // user not existed with the email
            return FALSE;
        }
    }

    /**
     * Checking for duplicate user by email address
     * @param String $email email to check in db
     * @return boolean
     */
    private function isUserExists($email) {
        $stmt = $this->conn->prepare("SELECT u_id from user WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    /**
     * Fetching user by email
     * @param String $email User email id
     */
    public function getUserByEmail($email) {
        $stmt = $this->conn->prepare("SELECT  u_id, email, name, pic_url, number, sms, api_key FROM user WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($user_id, $email, $name, $pic_url, $contact, $sms, $api_key);
            $stmt->fetch();
            $user = array();
            $user["u_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["api_key"] = $api_key;
            $user["sms"] = $sms;
            $user["number"] = $contact;
            $user["pic_url"] = $pic_url;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }

     public function getUserById($user_id) {
        $stmt = $this->conn->prepare("SELECT  email, name, pic_url, number, sms, api_key FROM user WHERE u_id = ?");
        $stmt->bind_param("s", $user_id);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($email, $name, $pic_url, $contact, $sms, $api_key);
            $stmt->fetch();
            $user = array();
            //$user["u_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["api_key"] = $api_key;
            $user["sms"] = $sms;
            $user["number"] = $contact;
            $user["pic_url"] = $pic_url;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }



    /**
     * Fetching user api key
     * @param String $user_id user id primary key in user table
     */
    public function getApiKeyById($user_id) {
        $stmt = $this->conn->prepare("SELECT api_key FROM users WHERE id = ?");
        $stmt->bind_param("i", $user_id);
        if ($stmt->execute()) {
            // $api_key = $stmt->get_result()->fetch_assoc();
            // TODO
            $stmt->bind_result($api_key);
            $stmt->close();
            return $api_key;
        } else {
            return NULL;
        }
    }

    /**
     * Fetching user id by api key
     * @param String $api_key user api key
     */
    public function getUserId($api_key) {
        $stmt = $this->conn->prepare("SELECT u_id FROM user WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        if ($stmt->execute()) {
            $stmt->bind_result($user_id);
            $stmt->fetch();
            // TODO
            // $user_id = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user_id;
        } else {
            return NULL;
        }
    }

    /**
     * Validating user api key
     * If the api key is there in db, it is a valid key
     * @param String $api_key user api key
     * @return boolean
     */
    public function isValidApiKey($api_key) {
        $stmt = $this->conn->prepare("SELECT u_id from user WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    /**
     * Generating random Unique MD5 String for user Api key
     */
    private function generateApiKey() {
        return md5(uniqid(rand(), true));
    }

    /* ------------- `tasks` table method ------------------ */

    /**
     * Creating new task
     * @param String $user_id user id to whom task belongs to
     * @param String $task task text
     */
    public function createTask($user_id, $task, $description) {
        $stmt = $this->conn->prepare("INSERT INTO question(title, description) VALUES(?, ?)");
        $stmt->bind_param("ss", $task, $description);
        $result = $stmt->execute();
        $stmt->close();

        if ($result) {
            // task row created
            // now assign the task to user
            $new_task_id = $this->conn->insert_id;
            $res = $this->createUserTask($user_id, $new_task_id);
            if ($res) {
                // task created successfully
                return $new_task_id;
            } else {
                // task failed to create
                return NULL;
            }
        } else {
            // task failed to create
            return NULL;
        }
    }

    public function createTaskAnswer($user_id, $task, $q_id) {
        $stmt = $this->conn->prepare("INSERT INTO answer(ans) VALUES(?)");
        $stmt->bind_param("s", $task);
        $result = $stmt->execute();
        $stmt->close();

        if ($result) {
            // task row created
            // now assign the task to user
            $new_task_id = $this->conn->insert_id;
            $res = $this->createUserTaskAnswer($user_id, $new_task_id);
            $res1 = $this->createQuestionAnswer($q_id, $new_task_id);
            if ($res) {
                // task created successfully
                return $new_task_id;
            } else {
                // task failed to create
                return NULL;
            }
        } else {
            // task failed to create
            return NULL;
        }
    }

    public function createTagQuest($tag, $quest) {
        require_once 'PassHash.php';
        $response = array();
        //$email = urldecode($email);
        // First check if user already existed in db
        //if (!$this->isUserExists($email)) {
            // Generating password hash
            //$password_hash = PassHash::hash($password);

            // Generating API key
            $api_key = $this->generateApiKey();
            
            //$refferal_code = 'ASHU10';
            $points = 0;
            $user_id = '';

            // insert query
            $stmt = $this->conn->prepare("INSERT INTO belongs(t_id, q_id) values(?, ?)");
            $stmt->bind_param("ii", $tag, $quest);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_CREATE_FAILED;
            }
        

        return $response;
    }


    /**
     * Fetching single task
     * @param String $task_id id of the task
     */
    public function getTask($task_id, $user_id) {
        $stmt = $this->conn->prepare("SELECT t.q_id, t.title, t.description, t.created_at from question t, user_question ut WHERE t.q_id = ? AND ut.q_id = q.q_id AND ut.u_id = ?");
        $stmt->bind_param("ii", $task_id, $user_id);
        if ($stmt->execute()) {
            $res = array();
            $stmt->bind_result($id, $task, $status, $created_at);
            // TODO
            // $task = $stmt->get_result()->fetch_assoc();
            $stmt->fetch();
            $res["id"] = $id;
            $res["task"] = $task;
            $res["status"] = $status;
            $res["created_at"] = $created_at;
            $stmt->close();
            return $res;
        } else {
            return NULL;
        }
    }


public function updateUserPoints($user_id, $points) {
        $stmt = $this->conn->prepare("UPDATE question set upvotes = ? where q_id = ?");
        $stmt->bind_param("ii", $points, $user_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }


    public function updateAnswerUpvotes($user_id, $points) {
        $stmt = $this->conn->prepare("UPDATE answer set ans_upvotes = ? where a_id = ?");
        $stmt->bind_param("ii", $points, $user_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }
    /**
     * Fetching all user tasks
     * @param String $user_id id of the user
     */
    public function getAllUserTasks($user_id) {
        $stmt = $this->conn->prepare("SELECT q.* FROM question q, user_question uq WHERE q.q_id = uq.q_id AND uq.u_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }

    public function getAllQuestions() {
        
        $stmt = $this->conn->prepare("SELECT q_id, title, description, upvotes from `question`");
        //$stmt->bind_param("i", $user_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['q_id'],$tasks['title'],$tasks['description'],$tasks['upvotes']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["q_id"] = $tasks["q_id"];
                $tmp["title"] = $tasks["title"];
                $tmp["description"] = $tasks["description"];
                $tmp["upvotes"] = $tasks["upvotes"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }

    public function getAllAnswers($ques_id) {
        
        $stmt = $this->conn->prepare("SELECT a_id,ans,ans_upvotes FROM answer where a_id in(SELECT a_id from question_answer where q_id = ?)");
        $stmt->bind_param("i", $ques_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['a_id'],$tasks['ans'],$tasks['ans_upvotes']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["a_id"] = $tasks["a_id"];
                $tmp["ans"] = $tasks["ans"];
                $tmp["ans_upvotes"] = $tasks["ans_upvotes"];
                //$tmp["upvotes"] = $tasks["upvotes"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }

public function getUserOfAnswer($ans_id) {
        
        $stmt = $this->conn->prepare("SELECT u_id,email,name, pic_url FROM user where u_id in(SELECT u_id from user_answer where a_id = ?)");
        $stmt->bind_param("i", $ans_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['u_id'],$tasks['email'],$tasks['name'], $tasks['pic_url']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["u_id"] = $tasks["u_id"];
                $tmp["email"] = $tasks["email"];
                $tmp["name"] = $tasks["name"];
                $tmp["pic_url"] = $tasks["pic_url"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }

    public function getQuestionByTag($tag_id) {
        
        $stmt = $this->conn->prepare("SELECT q_id, title, description, upvotes FROM question where q_id in(SELECT q_id from belongs where t_id = ?)");
        $stmt->bind_param("i", $tag_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['q_id'],$tasks['title'],$tasks['description'], $tasks['upvotes']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["q_id"] = $tasks["q_id"];
                $tmp["title"] = $tasks["title"];
                $tmp["description"] = $tasks["description"];
                $tmp["upvotes"] = $tasks["upvotes"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }

    public function getAllTags($ques_id) {
        
        $stmt = $this->conn->prepare("SELECT t_id,name FROM tags where t_id in(SELECT t_id from belongs where q_id = ?)");
        $stmt->bind_param("i", $ques_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['t_id'],$tasks['name']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["t_id"] = $tasks["t_id"];
                $tmp["name"] = $tasks["name"];
                //$tmp["ans_upvotes"] = $tasks["ans_upvotes"];
                //$tmp["upvotes"] = $tasks["upvotes"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }



    public function getUserOfQuestion($ques_id) {
        
        $stmt = $this->conn->prepare("SELECT u_id,email,name, pic_url FROM user where u_id in(SELECT u_id from user_question where q_id = ?)");
        $stmt->bind_param("i", $ques_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['u_id'],$tasks['email'],$tasks['name'], $tasks['pic_url']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["u_id"] = $tasks["u_id"];
                $tmp["email"] = $tasks["email"];
                $tmp["name"] = $tasks["name"];
                $tmp["pic_url"] = $tasks["pic_url"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }

    public function getQuestion($ques_id) {
        
        $stmt = $this->conn->prepare("SELECT q_id,title,description FROM question where q_id = ?)");
        $stmt->bind_param("i", $ques_id);
        $stmt->execute();
        $tasks = array();
        $stmt->bind_result($tasks['q_id'],$tasks['title'],$tasks['description']);

        

        $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["q_id"] = $tasks["q_id"];
                $tmp["title"] = $tasks["title"];
                $tmp["description"] = $tasks["description"];
                //$tmp["pic_url"] = $tasks["pic_url"];
                array_push($response["tasks"], $tmp);
            }
        $stmt->close();
        return $response;

    }


/*public function getUserOfQuestion($ques_id) {
        
        $stmt = $this->conn->prepare("SELECT email, name, pic_url, number, sms, api_key FROM user u, user_question uq WHERE uq.q_id = ? AND uq.u_id = u.u_id ");
        //$stmt->bind_param("i", $user_id);
         $stmt->bind_param("s", $ques_id);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($email, $name, $pic_url, $contact, $sms, $api_key);
            $stmt->fetch();
            $user = array();
            //$user["u_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["api_key"] = $api_key;
            $user["sms"] = $sms;
            $user["number"] = $contact;
            $user["pic_url"] = $pic_url;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }

    }*/


public function getAnsOfQuestion($question_id) {
        
        $stmt = $this->conn->prepare("SELECT ans, ans_upvotes FROM answer u, question_answer uq WHERE uq.q_id = ? /*AND uq.a_id = u.a_id*/ ");
        //$stmt->bind_param("i", $user_id);
         $stmt->bind_param("s", $question_id);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result( $ans, $ans_upvotes);
            $stmt->fetch();
            $user = array();
            //$user["u_id"] = $user_id;
           // $user["a_id"] = $a_id;
            $user["ans"] = $ans;
            $user["ans_upvotes"] = $ans_upvotes;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }

    }

    
    

    /**
     * Updating task
     * @param String $task_id id of the task
     * @param String $task task text
     * @param String $status task status
     */

    public function getUpvotesById($task_id) {
        $stmt = $this->conn->prepare("SELECT upvotes FROM question WHERE q_id = ?");
        $stmt->bind_param("i", $task_id);
        if ($stmt->execute()) {
            // $api_key = $stmt->get_result()->fetch_assoc();
            // TODO
            $stmt->bind_result($upvotes);
            $stmt->close();
            return ($upvotes + 1);
        } else {
            return NULL;
        }
    }
    public function updateTask($user_id, $task_id, $task) {
        $res = $this->getUpvotesById($task_id);
        //$upvotes = $res + 1;
        $stmt = $this->conn->prepare("UPDATE question q, user_question uq set q.title = ?, q.upvotes = $res WHERE q.q_id = ? AND q.q_id = uq.q_id AND uq.u_id = ?");
        $stmt->bind_param("siii", $task, $q_id, $u_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }
    public function getStarters($coupon_id) {
		
        $stmt = $this->conn->prepare("SELECT starter1 from starters where coupon_id = ?");
        $stmt->bind_param("s", $coupon_id);
        $stmt->execute();
        //$tasks = $stmt->get_result();
		  
		$tasks = array();
		$stmt->bind_result($tasks['starter1']);
		$response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $stmt->fetch()) {
                $tmp = array();
                $tmp["starter1"] = $tasks["starter1"];
                array_push($response["tasks"], $tmp);
            }
		$stmt->close();
        return $response;

    }

    /**
     * Deleting a task
     * @param String $task_id id of the task to delete
     */
    public function deleteTask($user_id, $task_id) {
        $stmt = $this->conn->prepare("DELETE t FROM tasks t, user_tasks ut WHERE t.id = ? AND ut.task_id = t.id AND ut.user_id = ?");
        $stmt->bind_param("ii", $task_id, $user_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }

    /* ------------- `user_tasks` table method ------------------ */

    /**
     * Function to assign a task to user
     * @param String $user_id id of the user
     * @param String $task_id id of the task
     */
    public function createUserTask($user_id, $task_id) {
        $stmt = $this->conn->prepare("INSERT INTO user_question(u_id, q_id) values(?, ?)");
        $stmt->bind_param("ii", $user_id, $task_id);
        $result = $stmt->execute();

        if (false === $result) {
            die('execute() failed: ' . htmlspecialchars($stmt->error));
        }
        $stmt->close();
        return $result;
    }

    public function createUserTaskAnswer($user_id, $task_id) {
        $stmt = $this->conn->prepare("INSERT INTO user_answer(u_id, a_id) values(?, ?)");
        $stmt->bind_param("ii", $user_id, $task_id);
        $result = $stmt->execute();

        if (false === $result) {
            die('execute() failed: ' . htmlspecialchars($stmt->error));
        }
        $stmt->close();
        return $result;
    }

    public function createQuestionAnswer($quest_id, $task_id) {
        $stmt = $this->conn->prepare("INSERT INTO question_answer(q_id, a_id) values(?, ?)");
        $stmt->bind_param("ii", $quest_id, $task_id);
        $result = $stmt->execute();

        if (false === $result) {
            die('execute() failed: ' . htmlspecialchars($stmt->error));
        }
        $stmt->close();
        return $result;
    }
}

?>
