package com.app2youth.hackaton.workup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nuriel on 22-May-15.
 */
public class GridViewAdapter extends ArrayAdapter<String> {

    private TextView tv_prof;
    public static int LAST_POSITION_PRESSED;
    public static String SUBJECT_ENTERED;
    public static String TEACHER_ENTERED;
    public static Boolean IS_ENTERED=false;

    private final Context context;
    private final String[] fields;
    private final ArrayList<String> arr_list_fields;

    
    //Constructor
    //---------------------------------------------------------------------------------------
    public GridViewAdapter(Context _context, String[] _fields)
            {
            super(_context, R.layout.grid_layout, _fields);
            this.context = _context;
            this.fields = _fields;
            this.arr_list_fields = new ArrayList<String>();
            arrayToArrayList();
            }
    public GridViewAdapter(Context _context, ArrayList<String> _fields)
            {
            super(_context, R.layout.grid_layout, _fields);
            this.context = _context;
            this.fields = null;
            this.arr_list_fields = _fields;
            }
    //---------------------------------------------------------------------------------------

    public View getView(final int position, View convertView, ViewGroup parent)
            {
            LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.grid_layout, parent, false);

            this.tv_prof = (TextView) rowView.findViewById(R.id.textView);

            handleArrayList(position);
            if(position>=7){
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(IS_ENTERED) {
                            sendData(SUBJECT_ENTERED,TEACHER_ENTERED,position/7,position%7, position);
                        }
                    }
                });
            }

            return rowView;
            }
    public void sendData(String subject, String teacher, int hour, int day, int position){
        /*
        String before = fields[position];
        Log.d("Msg",subject+","+teacher+","+hour+","+day+","+position+","+before);

        String sday="";
        if (day==0)sday="sunday";
        else if (day==1)sday="monday";
        else if (day==2)sday="tuesday";
        else if (day==3)sday="wednesday";
        else if (day==4)sday="thursday";
        else if (day==5)sday="friday";
        else if (day==6)sday="saturday";

        if (before == null || "---".equals(before)){

            Manager.system.addLesson(sday,hour,subject);
        }
        else if (Manager.type==0){
            Manager.system.addLesson(sday,hour,subject);
        }
        else if (Manager.type==1){
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year=cal.get(Calendar.YEAR);
            int month=cal.get(Calendar.MONTH)+1;
            int dayplus=cal.get(Calendar.DAY_OF_MONTH)+3;

            ((TeacherSystem) Manager.system).uploadTask(new Task(subject, subject, subject, dayplus, month, year, day, hour));

        }
        */
        //tv_prof.findViewById(R.id.gridView1).invalidate();
    }

    public void handleArrayList(int position)
            {
            setText(position);
            }

    public void setText(int position)
            {
            tv_prof.setText(arr_list_fields.get(position));
            }


    private void arrayToArrayList()
            {
            for(int i = 0; i < this.fields.length; i++)
            {
            this.arr_list_fields.add(this.fields[i]);
            }
            }

}