package com.app2youth.hackaton.Workup1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
		SQL.statement.execute("INSERT INTO classes (teachGroup, day, startTime) VALUES (" + groupID + ", " + day + ", '" + startingTime + "');");
		int classID = SQL.getLastID();
		
		int scheduleID = -1;
		ResultSet rs = SQL.statement.executeQuery("SELECT schedule FROM groups WHERE groupID = " + groupID + ";");
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
	
	public static void addTask(int groupID, String title, String description, String filingDate, Context context) throws SQLException{
		SQL.statement.execute("INSERT INTO tasks (title, description, teachGroup, filingDate, comments, ranksum, votes) VALUES ('" + title + "', '" + description + "', " + groupID + ", '" + filingDate + "', '', 0,0);");


		int taskID = SQL.getLastID();
		
		SQL.statement.execute("UPDATE groups SET tasks = CONCAT(tasks, '"+taskID+";') where groupID = "+groupID+";");
		
		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = " + groupID + ";");
		String students = null;
		while(rs.next())
			students=rs.getString(1);
		if (students==null || students.equals(""))
            return;

		String[] studentList = students.split(";");
		for (String studentID:studentList)
			addTaskToStudent(taskID, Integer.parseInt(studentID), context);
	}

	/*
	public static void addTask(int groupID, String title, String description, String filingDate, Bitmap image) throws SQLException{
		SQL.con.setAutoCommit(false);
		SQL.statement.execute("INSERT INTO tasks (title, description, teachGroup, filingDate, comments, ranksum, votes) VALUES ('" + title + "', '" + description + "', " + groupID + ", '" + filingDate + "', '', 0,0);");
		String sql = "INSERT INTO tasks (title, description, teachGroup, filingDate, comments, ranksum, votes, image) VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement stmt = SQL.con.prepareStatement(sql);

		stmt.setString(1, title);
		stmt.setString(2, description);
		stmt.setInt(3, groupID);
		stmt.setString(4, filingDate);
		stmt.setString(5, "");
		stmt.setInt(6, 0);
		stmt.setInt(7, 0);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] imageBytes = stream.toByteArray();
		Log.d("SHIT", "LENGTH: "+imageBytes.length);
		stmt.setObject(8, imageBytes);

		SQL.con.commit();


		int taskID = SQL.getLastID();

		SQL.statement.execute("UPDATE groups SET tasks = CONCAT(tasks, '"+taskID+";') where groupID = "+groupID+";");

		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = " + groupID + ";");
		String students = null;
		while(rs.next())
			students=rs.getString(1);
		if (students==null || students.equals(""))
			return;

		String[] studentList = students.split(";");
		for (String studentID:studentList)
			addTaskToStudent(taskID, Integer.parseInt(studentID));
	}
*/

	public static void addTask(int groupID, String title, String description, String filingDate, String image, Context context) throws SQLException{
		SQL.statement.execute("INSERT INTO tasks (title, description, teachGroup, filingDate, comments, ranksum, votes, taskImage) VALUES ('" + title + "', '" + description + "', " + groupID + ", '" + filingDate + "', '', 0,0, '"+image+"');");


		int taskID = SQL.getLastID();

		SQL.statement.execute("UPDATE groups SET tasks = CONCAT(tasks, '"+taskID+";') where groupID = "+groupID+";");

		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = " + groupID + ";");
		String students = null;
		while(rs.next())
			students=rs.getString(1);
		if (students==null || students.equals(""))
			return;

		String[] studentList = students.split(";");
		for (String studentID:studentList)
			addTaskToStudent(taskID, Integer.parseInt(studentID), context);
	}
	
	private static void addTaskToStudent(int taskID, int studentID, Context context) throws SQLException{
		SQL.statement.execute("UPDATE students SET tasks = CONCAT(tasks, '" + taskID + ";') where studentID = " + studentID + ";");
		addNotification(context.getString(R.string.push_new_task), context.getString(R.string.push_new_task_description), studentID, false);
	}
	
	
	
	public static void addStudent(String name, String fname, String phone) throws SQLException{
		SQL.statement.execute("INSERT INTO students (name, fname, phone, groups, tasks, grades) VALUES ('" + name + "', '" + fname + "', '" + phone + "', '', '', '')");
	}
	
	public static void addStudentToGroup(int studentID, int groupID) throws SQLException{
		SQL.statement.execute("UPDATE students SET groups = CONCAT(groups, '"+groupID+";') where studentID = "+studentID+";");
		SQL.statement.execute("UPDATE groups SET students = CONCAT(students, '"+studentID+";') where groupID = "+groupID+";");
	}
	
	public static void addComment(String comment, int taskID, boolean student, int commentorID, boolean isPublic) throws SQLException{
		int isStudent = student ? 1 : 0;
		int isPublicInt = isPublic ? 1 : 0;
		SQL.statement.execute("INSERT INTO comments (comment, isStudent, commentor, comments, isPublic) VALUES ('"+comment+"', "+isStudent+", "+commentorID+", '', "+isPublicInt+");");
		int commentID = SQL.getLastID();
		SQL.statement.execute("UPDATE tasks SET comments = CONCAT(comments, '"+commentID+";') where taskID = "+taskID+";");
	}

	public static void addCommentToComment(String comment, int commentedCommentID, boolean student, int commentorID, boolean isPublic) throws SQLException{
		int isStudent = student ? 1 : 0;
		int isPublicInt = isPublic ? 1 : 0;
		SQL.statement.execute("INSERT INTO comments (comment, isStudent, commentor, comments, isPublic) VALUES ('"+comment+"', "+isStudent+", "+commentorID+", '', "+isPublicInt+");");
		int commentID = SQL.getLastID();
		SQL.statement.execute("UPDATE comments SET comments = CONCAT(comments, '"+commentID+";') where commentID = "+commentedCommentID+";");
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
		if (groups==null)
			return "";
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
		ResultSet rs = SQL.statement.executeQuery("SELECT teacherID FROM teachers WHERE phone = '" + phone + "'");
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
	public static String getTasksFromStudent(int studentID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM students WHERE studentID = " + studentID + ";");
		String tasks=null;
		while(rs.next())
			tasks=rs.getString(1);

		return tasks;
	}
	public static String getTasksFromGroup(int groupID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM groups WHERE groupID = " + groupID + ";");
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

	public static String getCommentsFromComment(int commentID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT comments FROM comments WHERE commentID = " + commentID + ";");
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
		ResultSet rs = SQL.statement.executeQuery("SELECT students FROM groups WHERE groupID = " + id + ";");
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

	private static void deleteGradeForStudent(int studentID, int gradeID) throws SQLException{
		String[] grades = getGradesFromStudent(studentID);

		String gradesString="";
		for(int i=0; i<grades.length; i++){
			if (!grades[i].equals(""+gradeID))
				gradesString+=grades[i]+";";
		}

		SQL.statement.execute("UPDATE students SET grades = '"+gradesString+"' where studentID = "+studentID+";");
	}
	private static void deleteGroupOnlyForStudent(int studentID, int groupID) throws SQLException{
		String[] groups = getGroupIDsForStudent(studentID);

		String groupsString="";
		for(int i=0; i<groups.length; i++){
			if (!groups[i].equals(""+groupID))
				groupsString+=groups[i]+";";
		}

		SQL.statement.execute("UPDATE students SET groups = '"+groupsString+"' where studentID = "+studentID+";");

		String[] tasks = getTasksFromGroup(groupID).split(";");
		for (String task:tasks)
			if (task!=null && !task.equals(""))
				deleteTaskFromStudent(studentID, Integer.parseInt(task));
	}

	private static void deleteGroupOnlyForTeacher(int teacherID, int groupID) throws SQLException{
		String[] groups = getGroupIDsForTeacher(teacherID);

		String groupsString="";
		for(int i=0; i<groups.length; i++){
			if (!groups[i].equals(""+groupID))
				groupsString+=groups[i]+";";
		}

		SQL.statement.execute("UPDATE teachers SET groups = '" + groupsString + "' where teacherID = " + teacherID + ";");
	}

	public static int getScheduleFromGroup(int groupID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT schedule FROM groups WHERE groupID = " + groupID + ";");
		int id=-1;
		while(rs.next()){
			id = rs.getInt(1);
		}
		return id;
	}

	public static void deleteGroup(int groupID) throws SQLException {
		int scheduleID = getScheduleFromGroup(groupID);
		ResultSet classesrs = SQL.statement.executeQuery("SELECT week FROM schedules WHERE scheduleID = " + scheduleID + ";");
		while(classesrs.next()){
			String[] classes = classesrs.getString(1).split(";");
			for (String clazz:classes){
				if (!clazz.equals(""))
					SQL.spareStatement.execute("DELETE FROM classes WHERE classID = " + clazz + ";");
			}

		}
		SQL.statement.execute("DELETE FROM schedules WHERE scheduleID = " + scheduleID + ";");

		String[] students = getStudentsFromGroup(groupID);
		for (int i=0; i<students.length; i++){
			if (!students[i].equals(""))
				deleteGroupOnlyForStudent(Integer.parseInt(students[i]), groupID);
		}

		deleteGroupOnlyForTeacher(getGroupTeacher(groupID), groupID);


		ResultSet rs = SQL.spareStatement.executeQuery("SELECT gradeID FROM grades WHERE teachGroup = " + groupID+";");
		while(rs.next()){
			for (int i=0; i<students.length; i++){
				if (!students[i].equals(""))
					deleteGradeForStudent(Integer.parseInt(students[i]), rs.getInt(1));
			}
		}


		Log.d("SHIT", "IM HEREEEEE, DELETINGING");
		SQL.spareStatement.execute("DELETE FROM groups WHERE groupID = " + groupID + ";");
		SQL.spareStatement.execute("DELETE FROM grades WHERE teachGroup = "+groupID+";");
		SQL.spareStatement.execute("DELETE FROM tasks WHERE teachGroup = "+groupID+";");

	}

	public static String getGroupName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT name FROM groups WHERE groupID = '" + id + "' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}

	public static int getGroupTeacher(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT teacher FROM groups WHERE groupID = '" + id + "' LIMIT 1;");
		int tid=-1;
		while(rs.next()){
			tid = rs.getInt(1);
		}
		return tid;
	}

	public static void addGrade(int studentID, int groupID, String description, int grade, Context context) throws SQLException{
		SQL.statement.execute("INSERT INTO grades (teachGroup, description, grade) VALUES ("+groupID+", '"+description+"', "+grade+");");
		int commentID = SQL.getLastID();
		SQL.statement.execute("UPDATE students SET grades = CONCAT(grades, '"+commentID+";') where studentID = "+studentID+";");

		addNotification(context.getString(R.string.push_new_grade), context.getString(R.string.push_new_grade_description)+getGroupName(groupID), studentID, false);
	}


	public static String getTeacherName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT name FROM teachers WHERE teacherID = '"+id+"' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}
	public static String getTeacherNameSpare(int id) throws SQLException{
		ResultSet rs = SQL.spareStatement.executeQuery("SELECT name FROM teachers WHERE teacherID = '"+id+"' LIMIT 1;");
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
	public static String getStudentLastName(int id) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT fname FROM students WHERE studentID = '"+id+"' LIMIT 1;");
		String name="";
		while(rs.next()){
			name = rs.getString(1);
		}
		return name;
	}


	public static String[] getGradesFromStudent(int studentID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT grades FROM students WHERE studentID = " + studentID+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new String[0];

		String[] ids = groups.split(";");

		return ids;
	}

	public static ArrayList<Integer> getGradesFromStudentInGroup(int studentID, int groupID) throws SQLException{
		ResultSet rs = SQL.statement.executeQuery("SELECT grades FROM students WHERE studentID = " + studentID+ ";");
		String groups=null;
		while(rs.next())
			groups=rs.getString(1);

		if (groups==null || groups.length()==0)
			return new ArrayList<Integer>();

		String[] ids = groups.split(";");

		ArrayList<Integer> idsWithGroup = new ArrayList<Integer>();
		for (String id:ids){
			if (!id.equals("") && id!=null)
				if (getGroupFromGrade(Integer.parseInt(id))==groupID)
					idsWithGroup.add(Integer.parseInt(id));
		}
		return idsWithGroup;
	}

	public static int getGrade(int gradeID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT grade FROM grades WHERE gradeID = " + gradeID+ ";");
		int grade=0;
		while(rs.next())
			grade=rs.getInt(1);
		return grade;
	}
	public static String getGradeDescription(int gradeID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT description FROM grades WHERE gradeID = " + gradeID+ ";");
		String grade=null;
		while(rs.next())
			grade=rs.getString(1);
		return grade;
	}
	public static int getGroupFromGrade(int gradeID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT teachGroup FROM grades WHERE gradeID = " + gradeID+ ";");
		int group=0;
		while(rs.next())
			group=rs.getInt(1);
		return group;
	}


	public static void finishTaskAndSendFeedback(int taskID, int rating) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT ranksum,votes FROM tasks WHERE taskID = " + taskID+ ";");
		int ranksum=0;
		int votes=0;
		while(rs.next()) {
			ranksum = rs.getInt(1);
			votes = rs.getInt(2);
		}
		ranksum+=rating;
		votes++;

		SQL.statement.execute("UPDATE tasks SET ranksum = "+ranksum+", votes = "+votes+" where taskID = "+taskID+";");
	}

	public static double getAverageTaskGrade(int taskID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT ranksum,votes FROM tasks WHERE taskID = " + taskID+ ";");
		int ranksum=0;
		int votes=0;
		while(rs.next()) {
			ranksum = rs.getInt(1);
			votes = rs.getInt(2);
		}

		return votes==0? 0:(double)ranksum/(double)votes;
	}
	public static String getTaskTitle(int taskID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT title FROM tasks WHERE taskID = " + taskID+ ";");
		String title=null;
		while(rs.next()) {
			title = rs.getString(1);
		}
		return title;
	}

	public static void deleteTaskFromStudent(int studentID, int taskID) throws SQLException {
		String[] tasks = getTasksFromStudent(studentID).split(";");

		String tasksString="";
		for(int i=0; i<tasks.length; i++){
			if (!tasks[i].equals(""+taskID))
				tasksString+=tasks[i]+";";
		}
		SQL.statement.execute("UPDATE students SET tasks = '"+tasksString+"' where studentID = "+studentID+";");
	}

	public static void deleteTask(int taskID, int groupID) throws SQLException {
		Log.d("SHIT", taskID+", "+groupID);
		String[] tasks = getTasksFromGroup(groupID).split(";");

		String tasksString="";
		for(int i=0; i<tasks.length; i++){
			if (!tasks[i].equals(""+taskID))
				tasksString+=tasks[i]+";";
		}


		String[] students = getStudentsFromGroup(groupID);
		for (String student:students)
			deleteTaskFromStudent(Integer.parseInt(student), taskID);

		SQL.statement.execute("UPDATE groups SET tasks = '"+tasksString+"' where groupID = "+groupID+";");

		SQL.statement.execute("DELETE FROM tasks WHERE taskID = " + taskID + ";");
	}

	public static int getTaskFinisherAmount(int taskID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT votes FROM tasks WHERE taskID = " + taskID + ";");
		int votes=-1;
		while(rs.next()) {
			votes = rs.getInt(1);
		}
		return votes;
	}

	public static int getGroupFromTask(int taskID) throws SQLException {
		ResultSet rs = SQL.statement.executeQuery("SELECT teachGroup FROM tasks WHERE taskID = " + taskID + ";");
		int group=-1;
		while(rs.next()) {
			group = rs.getInt(1);
		}
		return group;
	}

	public static ArrayList<String> getTaskFinishers(int taskID) throws SQLException {
		int groupID = getGroupFromTask(taskID);
		String[] studentIDs = getStudentsFromGroup(groupID);
		if (studentIDs==null || (studentIDs.length==1 && studentIDs[0].equals("")))
			return new ArrayList<String>();

		ArrayList<String> students = new ArrayList<String>();
		mainloop:
		for (String studentID:studentIDs){
			String[] studentTasks = getTasksFromStudent(Integer.parseInt(studentID)).split(";");
			if (studentTasks==null)
				continue;
			for (String studentTask:studentTasks){
				if (studentTask!=null && !studentTask.equals("") && Integer.parseInt(studentTask) == taskID){
					students.add(getStudentName(Integer.parseInt(studentID))+" "+getStudentLastName(Integer.parseInt(studentID)));
					continue mainloop;
				}
			}
		}
		return students;
	}


	public static void addNotification(String title, String message, int recipient, boolean recipientIsTeacher) throws SQLException{
		int isTeacher = recipientIsTeacher? 1:0;
		SQL.statement.execute("INSERT INTO notifications (title, message, recipient, isTeacher) VALUES ('"+title+"', '"+message+"', "+recipient+", "+isTeacher+");");
	}

	public static String[] checkForNotifications(int id, boolean isTeacher) throws SQLException{
		int teacher = isTeacher? 1:0;
		ResultSet rs = SQL.pushStatemenet.executeQuery("SELECT title, message FROM notifications WHERE recipient = " + id + " AND isTeacher = " + teacher+";");
		String title="";
		String message="";
		boolean pushHappened=false;
		while(rs.next()){
			pushHappened=true;
			title=rs.getString(1);
			message=rs.getString(2);

		}

		if (pushHappened)
			SQL.pushStatemenet.execute("DELETE FROM notifications WHERE recipient = " + id + " AND isTeacher = " + teacher + ";");

		return new String[]{title, message};
	}

	public static int getCommentorID(int comment) throws SQLException {
		ResultSet rs = SQL.pushStatemenet.executeQuery("SELECT commentor FROM comments WHERE commentID = "+comment+";");
		int id=-1;
		while(rs.next()){
			id = rs.getInt(1);
		}
		return id;
	}
	public static boolean getCommentorType(int comment) throws SQLException {
		ResultSet rs = SQL.pushStatemenet.executeQuery("SELECT isStudent FROM comments WHERE commentID = "+comment+";");
		boolean type=false;
		while(rs.next()){
			type = !rs.getBoolean(1);
		}
		return type;
	}

	public static void deleteTeacher(int teacherID) throws SQLException {
		String[] groups = getGroupIDsForTeacher(teacherID);
		for (String group:groups){
			if (!group.equals(""))
				deleteGroup(Integer.parseInt(group));
		}

		SQL.statement.execute("DELETE FROM teachers WHERE teacherID = "+teacherID+";");
	}

	public static void deleteStudent(int studentID) throws SQLException {
		String[] groups = getGroupIDsForStudent(studentID);
		for (String group:groups)
			if (!group.equals(""))
				deleteStudentFromGroup(studentID, Integer.parseInt(group));

		SQL.statement.execute("DELETE FROM students WHERE studentID = "+studentID+";");
		SQL.statement.execute("DELETE FROM notifications WHERE recipient = "+studentID+" AND isTeacher = 0;");
	}

}


