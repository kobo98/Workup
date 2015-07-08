package com.app2youth.hackaton.workup;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
	
	private Controller(){}
	
	public static void createTables() throws SQLException{
		
		createTeachers();
		createStudents();
		createSchedules();
		createClasses();
		createGroups();
		createTasks();
		createComments();
	}
	
	private static void createTeachers() throws SQLException{
		SQL.statement.execute("CREATE TABLE teachers("
				+ "`teacherID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`name` varchar(255) NOT NULL, "
				+ "`fname` varchar(255) NOT NULL, "
				+ "`phone` varchar(255) NOT NULL, "
				+ "`groups` varchar(255) NOT NULL"
				+ ");");
	}
	private static void createStudents() throws SQLException{
		SQL.statement.execute("CREATE TABLE students("
				+ "`studentID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`name` varchar(255) NOT NULL, "
				+ "`fname` varchar(255) NOT NULL, "
				+ "`phone` varchar(255) NOT NULL, "
				+ "`groups` varchar(255) NOT NULL, "
				+ "`tasks` varchar(255) NOT NULL"
				+ ");");
	}
	private static void createSchedules() throws SQLException{
		SQL.statement.execute("CREATE TABLE schedules("
				+ "`scheduleID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`week` varchar(5000) NOT NULL"
				+ ");");
	}
	private static void createClasses() throws SQLException{
		SQL.statement.execute("CREATE TABLE classes("
				+ "`classID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`teachGroup` int NOT NULL, "
				+ "`day` int NOT NULL, "
				+ "`startTime` varchar(255) NOT NULL"
				+ ");");
	}
	private static void createGroups() throws SQLException{
		SQL.statement.execute("CREATE TABLE groups("
				+ "`groupID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`name` varchar(5000) NOT NULL, "
				+ "`teacher` int NOT NULL, "
				+ "`schedule` int NOT NULL, "
				+ "`students` varchar(255) NOT NULL, "
				+ "`tasks` varchar(5000) NOT NULL"
				+ ");");
	}
	private static void createTasks() throws SQLException{
		SQL.statement.execute("CREATE TABLE tasks("
				+ "`taskID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`title` varchar(255) NOT NULL, "
				+ "`description` varchar(1000) NOT NULL, "
				+ "`teachGroup` int NOT NULL, "
				+ "`filingDate` Date NOT NULL, "
				+ "`comments` varchar(5000) NOT NULL"
				+ ");");
	}
	private static void createComments() throws SQLException{
		SQL.statement.execute("CREATE TABLE comments("
				+ "`taskID` int PRIMARY KEY AUTO_INCREMENT, "
				+ "`comment` varchar(255) NOT NULL, "
				+ "`isStudent` BIT(1) NOT NULL, "
				+ "`commentor` varchar(255) NOT NULL"
				+ ");");
	}
	
	
	
	public static void addTeacher(String name, String fname, String phone) throws SQLException{
		SQL.statement.execute("INSERT INTO teachers (name, fname, phone, groups) VALUES ('"+name+"', '"+fname+"', '"+phone+"', '')");
	}
	
	private static void addSchedule() throws SQLException{
		SQL.statement.execute("INSERT INTO schedules (week) VALUES ('')");
	}
	
	public static void addClass(int groupID, int day, String startingTime) throws SQLException{
		SQL.statement.execute("INSERT INTO classes (teachGroup, day, startTime) VALUES ("+groupID+", "+day+", '"+startingTime+"');");
		int classID = SQL.getLastID();
		
		int scheduleID = -1;
		ResultSet rs = SQL.statement.executeQuery("SELECT schedule FROM groups WHERE groupID = "+groupID+";");
		while(rs.next())
			scheduleID=rs.getInt(1);
		
		SQL.statement.execute("UPDATE schedules SET week = CONCAT(week, '"+classID+";') where scheduleID = "+scheduleID+";");
	}
	
	public static void addGroup(int teacherID, String name) throws SQLException{
		addSchedule();
		int scheduleID = SQL.getLastID();
		SQL.statement.execute("INSERT INTO groups (name, teacher, schedule, students, tasks) VALUES ('"+name+"', "+teacherID+", "+scheduleID+", '', '');");
		int groupID = SQL.getLastID();
		SQL.statement.execute("UPDATE teachers SET groups = CONCAT(groups, '"+groupID+";') where teacherID = "+teacherID+";");
	}
	
	public static void addTask(int groupID, String title, String description, String filingDate) throws SQLException{
		SQL.statement.execute("INSERT INTO tasks (title, description, teachGroup, filingDate, comments) VALUES ('"+title+"', '"+description+"', "+groupID+", '"+filingDate+"', '');");
		int taskID = SQL.getLastID();
		
		SQL.statement.execute("UPDATE groups SET tasks = CONCAT(tasks, '"+taskID+";') where groupID = "+groupID+";");
		
		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = "+groupID+";");
		String students = null;
		while(rs.next())
			students=rs.getString(1);
		if (students==null || students.equals(""))
            return;

		String[] studentList = students.split(";");
		for (String studentID:studentList)
			addTaskToStudent(taskID, Integer.parseInt(studentID));
	}
	
	private static void addTaskToStudent(int taskID, int studentID) throws SQLException{
		SQL.statement.execute("UPDATE students SET tasks = CONCAT(tasks, '"+taskID+";') where studentID = "+studentID+";");
	}
	
	
	
	public static void addStudent(String name, String fname, String phone) throws SQLException{
		SQL.statement.execute("INSERT INTO students (name, fname, phone, groups, tasks) VALUES ('"+name+"', '"+fname+"', '"+phone+"', '', '')");
	}
	
	public static void addStudentToGroup(int studentID, int groupID) throws SQLException{
		SQL.statement.execute("UPDATE students SET groups = CONCAT(groups, '"+groupID+";') where studentID = "+studentID+";");
		SQL.statement.execute("UPDATE groups SET students = CONCAT(students, '"+studentID+";') where groupID = "+groupID+";");
	}
	
	public static void addComment(String comment, int taskID, boolean student, int commentorID) throws SQLException{
		int isStudent = student ? 1 : 0;
		SQL.statement.execute("INSERT INTO comments (comment, isStudent, commentor) VALUES ('"+comment+"', "+isStudent+", "+commentorID+");");
		int commentID = SQL.getLastID();
		SQL.statement.execute("UPDATE tasks SET comments = CONCAT(comments, '"+commentID+";') where taskID = "+taskID+";");
	}
	
	
	
	
	//User functions
	public static int getTeacherIDByPhone(String phone) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT teacherID FROM teachers WHERE phone = '"+phone+"' LIMIT 1;");
		int id=-1;
		while(rs.next()){
			id = rs.getInt(1);
		}
		return id;
	}
	
	public static int getStudentIDByPhone(String phone) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT studentID FROM students WHERE phone = '"+phone+"' LIMIT 1;");
		int id=-1;
		while(rs.next()){
			id = rs.getInt(1);
		}
		return id;
	}
	
	
	public static String getListOfClassesForTeacher(String phone) throws SQLException{
		return getListOfClassesForTeacher(getTeacherIDByPhone(phone));
	}
	
	public static String getListOfClassesForTeacher(int id) throws SQLException{
		ResultSet groupRS = SQL.statement.executeQuery("SELECT groups FROM teachers WHERE teacherID = "+id+" LIMIT 1;");
		
		String groups = null;
		while(groupRS.next())
			groups=groupRS.getString(1);
		
		String groupIN = groups.replace(";", ",");
        if (groupIN.length()>0){
            groupIN = groupIN.substring(0, groupIN.length()-1);
        }
		else
            groupIN="-1";
		
		ResultSet scheduleRS = SQL.statement.executeQuery("SELECT schedule FROM groups WHERE groupID IN ("+groupIN+");");
		
		String schedules = "";
		while(scheduleRS.next())
			schedules+=scheduleRS.getString(1)+",";
        if (schedules=="")
            return "";
        schedules = schedules.substring(0, schedules.length()-1);
		
		String scheduleIN = schedules;
        ResultSet weekRS = SQL.statement.executeQuery("SELECT week FROM schedules WHERE scheduleID IN ("+scheduleIN+");");
		
		String weeks = "";
		while(weekRS.next())
			weeks+=weekRS.getString(1);
        if (weeks.length()>0)
            weeks = weeks.substring(0, weeks.length()-1);
		
		
		return weeks;
	}
	
	
	public static String getListOfClassesForStudent(String phone) throws SQLException{
		return getListOfClassesForStudent(getStudentIDByPhone(phone));
	}
	
	public static String getListOfClassesForStudent(int id) throws SQLException{
		ResultSet groupRS = SQL.statement.executeQuery("SELECT groups FROM students WHERE studentID = "+id+" LIMIT 1;");
		
		String groups = null;
		while(groupRS.next())
			groups=groupRS.getString(1);
		
		String groupIN = groups.replace(";", ",");
        if (groupIN.length()>0)
            groupIN = groupIN.substring(0, groupIN.length()-1);
		else
            groupIN="-1";

		ResultSet scheduleRS = SQL.statement.executeQuery("SELECT schedule FROM groups WHERE groupID IN ("+groupIN+");");
		
		String schedules = "";
		while(scheduleRS.next())
			schedules+=scheduleRS.getString(1)+",";
        if (schedules=="")
            return "";
		schedules = schedules.substring(0, schedules.length()-1);
		
		
		String scheduleIN = schedules;
		
		ResultSet weekRS = SQL.statement.executeQuery("SELECT week FROM schedules WHERE scheduleID IN ("+scheduleIN+");");
		
		String weeks = "";
		while(weekRS.next())
			weeks+=weekRS.getString(1);
        if (weeks.length()>0)
            weeks = weeks.substring(0, weeks.length()-1);

		
		return weeks;
	}
	
	
	public static int getGroupIDByNameAndPhone(String name, String phone) throws SQLException{
		int teacherID = getTeacherIDByPhone(phone);
		return getGroupIDByNameAndPhone(name, teacherID);
	}
	
	public static int getGroupIDByNameAndPhone(String name, int teacherID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT groupID FROM groups WHERE name = '"+name+"' AND teacher = "+teacherID+" LIMIT 1;");
		int id=-1;
		while(rs.next()){
			id = rs.getInt(1);
		}
		return id;
	}
	
	public static boolean teacherExists(String phone) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT teacherID FROM teachers WHERE phone = '"+phone+"'");
		int id=-1;
		while(rs.next())
			id=rs.getInt(1);
		if (id==-1)
			return false;
		return true;
	}
	public static boolean studentExists(String phone) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT studentID FROM students WHERE phone = '"+phone+"'");
		int id=-1;
		while(rs.next())
			id=rs.getInt(1);
		if (id==-1)
			return false;
		return true;
	}
    public static String getTasksFromStudent(String phone) throws SQLException{
        ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM students WHERE phone = " + phone + ";");
        String tasks=null;
        while(rs.next())
            tasks=rs.getString(1);

        return tasks;
    }


    public static String getCommentsFromTask(int taskID) throws SQLException{
        ResultSet rs = SQL.statement.executeQuery("SELECT comments FROM tasks WHERE taskID = " + taskID + ";");
        String cmts=null;
        while(rs.next())
            cmts=rs.getString(1);

        return cmts;
    }

    public static String[] getGroupNamesForTeacher(String phone) throws SQLException{
        ResultSet rs = SQL.statement.executeQuery("SELECT groups FROM teachers WHERE teacherID = " + getTeacherIDByPhone(phone) + ";");
        String groups=null;
        while(rs.next())
            groups=rs.getString(1);

        if (groups==null || groups.length()==0)
            return new String[0];

        String[] ids = groups.split(";");
        String[] names = new String[ids.length];
        for(int i=0; i<ids.length; i++){
            ResultSet rs2 = SQL.statement.executeQuery("SELECT name FROM groups WHERE groupID = " + ids[i] + ";");
            String name="";
            while(rs2.next())
                name=rs2.getString(1);
            names[i]=name;
        }

        return names;
    }

	public static String[] getGroupNamesForStudent(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT groups FROM students WHERE studentID = " + id+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new String[0];

		String[] ids = groups.split(";");
		String[] names = new String[ids.length];
		for(int i=0; i<ids.length; i++){
			ResultSet rs2 = SQL.statement.executeQuery("SELECT name FROM groups WHERE groupID = " + ids[i] + ";");
			String name="";
			while(rs2.next())
				name=rs2.getString(1);
			names[i]=name;
		}

		return names;
	}
	public static String[] getGroupNamesForStudent(String phone) throws SQLException{
		return getGroupNamesForStudent(getStudentIDByPhone(phone));
	}

	public static String[] getGroupIDsForStudent(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT groups FROM students WHERE studentID = " + id+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new String[0];

		String[] ids = groups.split(";");

		return ids;
	}
	public static String[] getGroupIDsForStudent(String phone) throws SQLException{
		return getGroupIDsForStudent(getStudentIDByPhone(phone));
	}


	public static String[] getGroupIDsForTeacher(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT groups FROM teachers WHERE teacherID = " + id+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new String[0];

		String[] ids = groups.split(";");

		return ids;
	}
	public static String[] getGroupIDsForTeacher(String phone) throws SQLException{
		return getGroupIDsForTeacher(getTeacherIDByPhone(phone));
	}

	public static String[] getStudentsFromGroup(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = " + id+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new String[0];

		String[] ids = groups.split(";");

		return ids;
	}

	public static void deleteStudentFromGroup(int studentID, int groupID) throws SQLException{
		String[] groups = getGroupIDsForStudent(studentID);
		String[] students = getStudentsFromGroup(groupID);

		String groupsString="";
		for(int i=0; i<groups.length; i++){
			if (!groups[i].equals(""+groupID))
				groupsString+=groups[i]+";";
		}


		String studentsString="";
		for(int i=0; i<students.length; i++){
			if (!students[i].equals(""+studentID))
				studentsString+=students[i]+";";
		}

		SQL.statement.execute("UPDATE students SET groups = '"+groupsString+"' where studentID = "+studentID+";");
		SQL.statement.execute("UPDATE groups SET students = '"+studentsString+"' where groupID = "+groupID+";");
	}

	public static String getGroupName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT name FROM groups WHERE groupID = '"+id+"' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}

	public static int getGroupTeacher(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT teacher FROM groups WHERE groupID = '"+id+"' LIMIT 1;");
		int tid=-1;
		while(rs.next()){
			tid = rs.getInt(1);
		}
		return tid;
	}

	public static String getTeacherName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT name FROM teachers WHERE teacherID = '"+id+"' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}
	public static String getStudentName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT name FROM students WHERE studentID = '"+id+"' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}
}
