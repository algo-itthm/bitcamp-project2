package bitcamp.project2.controller;

import bitcamp.project2.util.Prompt;
import bitcamp.project2.vo.Plan;
import bitcamp.project2.vo.User;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import static bitcamp.project2.util.Prompt.*;

public class AppointmentController {
    UserController uc = UserController.getInstance();

    LinkedList<User> userList = uc.getUserList();
    LinkedList<String> memberList = new LinkedList<String>();
    LinkedList<String> appointmentList = new LinkedList<>();
    String appointment = "";

//    public class Plan
//        private int no;
//        private String title;
//        private Date startDate;
//        private Date endDate;
//        private String repeatedDays;
//
//
//
//    public class User
//        private String name;
//        private String password;
//        private java.sql.Date joinDate;
//        private LinkedList planList;



    ///////////////////////////////////////////////////////////
    ////////////////////// Constructor ////////////////////////
    ///////////////////////////////////////////////////////////
    AppointmentController(){

    }



    ///////////////////////////////////////////////////////////
    ////////////////////// getInstance() //////////////////////
    ///////////////////////////////////////////////////////////
    private static AppointmentController m;

    // setup AppointmentController Instance
    public static AppointmentController getInstance() {

        if (m == null) {
            m = new AppointmentController();
        }

        return m;
    }// Method getInstance END

    // reset AppointmentController Instance
    public static void freeInstance() {
        m = null;
    }// Method freeInstance END



    ///////////////////////////////////////////////////////////
    ///////////////////////// Method //////////////////////////
    ///////////////////////////////////////////////////////////
    public void appointment(){
        setMember();
        setDate();
    }

    //////////////////////// set Member ////////////////////////
    private void setMember(){
        String ans = "";
        memberList = new LinkedList<>();

        for(;;) {
            System.out.print(printSetMember());
            ans = getUserScanner();

            if(ans.equals("0")){
                return;
            }
            if (isExistMember(ans)) {
                if(!isDuplicateMember(ans)){
                    System.out.printf("'%s'님을 추가합니다. \n", ans);
                    memberList.add(ans);
                    continue;
                }
                System.out.printf("이미 존재하는 멤버입니다. \n");
                continue;
            }
            System.out.printf("존재하지 않는 멤버입니다. \n");
        }

    }

    private String printSetMember(){
        String str = "";

        str += "함께 할 사람(0: 다음) : ";

        return str;
    }

    private boolean isExistMember(String name){
        Iterator<User> iter = userList.iterator();
        User currentUser;
        while(iter.hasNext()) {
            currentUser = iter.next();

            if (name.equals(currentUser.getName()))
                return true;
        }
        return false;
    }

    private boolean isDuplicateMember(String name){
        Iterator<String> iter = memberList.iterator();
        String userName;
        while(iter.hasNext()) {
            userName = iter.next();

            if (name.equals(userName))
                return true;
        }
        return false;
    }

    //////////////////////// set Date ////////////////////////
    private void setDate(){
        int month = getMonth();
        if(month > 0){
            searchDate(month);
        }
    }

    private String printSetDate(){
        String str = "";

        str += "검색할 기간(월) : ";

        return str;
    }

    private int getMonth(){
        System.out.print(printSetDate());
        int month;
        String ans = getUserScanner();
        if(isValidateDate(ans)){
            month = Integer.parseInt(ans);
          return month;
        }
        return -1;
    }

    private boolean isValidateDate(String ans){
        try{
            int month = Integer.parseInt(ans);
            return month > 0 && month <= 12 ? true : false;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void searchDate(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, month - 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] tempAvailableDates = new int[lastDay];

        // 가능한 기간 추출
        for (int i = 0; i < memberList.size(); i++) {
            User user = getUserByName(memberList.get(i));
            if(user.getPlanList() != null) {
                for (int j = 0; j < user.getPlanList().size(); j++) {
                    Plan plan = (Plan) user.getPlanList().get(j);

                    calendar.setTime(plan.getStartDate());
                    if(calendar.get(Calendar.MONTH) == month - 1) {
                        int startDate = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.setTime(plan.getEndDate());
                        int endDate = calendar.get(Calendar.DAY_OF_MONTH);

                        for(int k = startDate - 1; k < endDate; k++) {
                            tempAvailableDates[k] = -1;
                        }
                    }
                }
            }
        }

        LinkedList<Plan> availableDates = new LinkedList<>();
        Date sDate = null, eDate = null;
        Plan plan = new Plan();
        for(int i = 0; i < lastDay - 1; i++) {
            if(tempAvailableDates[i] == 0) {
                if(sDate == null) {
                    sDate = Date.valueOf(String.format("2024-%02d-%02d", month, i + 1));
                }
                if(tempAvailableDates[i + 1] == -1) {
                    eDate = Date.valueOf(String.format("2024-%02d-%02d", month, i + 1));
                }
                if(i + 2 == lastDay && tempAvailableDates[i + 1] == 0) {
                    eDate = Date.valueOf(String.format("2024-%02d-%02d", month, i + 2));
                }

                if(sDate != null && eDate != null) {
                    plan.setStartDate(sDate);
                    plan.setEndDate(eDate);
                    availableDates.add(plan);

                    sDate = eDate = null;
                    plan = new Plan();
                }
            }
        }

        if(availableDates.isEmpty()) {
            System.out.println("가능한 일정이 없습니다.");
            return;
        }

        listAvailableDates(availableDates);
        if(Prompt.input("일정을 등록하시겠습니까?(y/n)").equalsIgnoreCase("y")) {
            plan = new Plan();
            plan.setTitle(Prompt.input("제목? "));
            appointment += plan.getTitle() + " ";

            setDates(plan, month);

            appointment += " ( ";

            for(String str : memberList) {
                appointment += str + " ";
                User user = getUserByName(str);
                user.getPlanList().add(plan);
            }

            appointment += ")";
            appointmentList.add(appointment);
            appointment = "";

            System.out.println("등록되었습니다.\n");
        }
    }

    public void setDates(Plan plan, int month) {
        int lastDay = printCalendar(2024, month);
        String days = Prompt.input("일?(1-%d 반복할요일)", lastDay);

        String startDate;
        String endDate;
        String repeatedDays = null;

        if (days.contains(" ")) {
            repeatedDays = days.split(" ")[1];
        }
        if (days.contains("-")) {
            startDate = String.format("2024-%d-%s", month, days.split("-")[0]);
            endDate = String.format("2024-%d-%s", month, days.split("-")[1].split(" ")[0]);
        } else {
            startDate = endDate = String.format("2024-%d-%s", month, days.split(" ")[0]);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");

        plan.setStartDate(Date.valueOf(startDate));
        plan.setEndDate(Date.valueOf(endDate));
        plan.setRepeatedDays(repeatedDays);

        if(startDate != endDate) {
            appointment += formatter.format(plan.getStartDate()) + " ~ " + formatter.format(plan.getEndDate());
        } else {
            appointment += formatter.format(plan.getStartDate());
        }

    }

    private void listAvailableDates(LinkedList<Plan> availableDates) {
        String line = "--------------------------------------------------";
        System.out.println(line);
        System.out.println("No\t\tDate");
        for(int i = 0; i < availableDates.size(); i++) {
            Plan plan = availableDates.get(i);
            String date = "";
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
            String startDate = formatter.format(plan.getStartDate());
            String endDate = formatter.format(plan.getEndDate());

            if(startDate.equals(endDate)) {
                date = startDate;
            } else {
                date = startDate + " ~ " + endDate;
            }

            System.out.printf("%d.\t\t%s\n", (i + 1), date);
        }
        System.out.println(line);
    }

    private User getUserByName(String name) {
        for (User user : userList) {
            if (Objects.equals(name, user.getName())) {
                return user;
            }
        }

        return null;
    }
    ///////////////////////////////////////////////////////////
    ///////////////// public getter, setter ///////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////////////////////////// -- ///////////////////////////
    //////////////////////////// -- ///////////////////////////
    //////////////////////////// -- ///////////////////////////
    //////////////////////// ---------- ///////////////////////
    ////////////////////////// ------ /////////////////////////
    //////////////////////////// -- ///////////////////////////
    ///////////////////////////////////////////////////////////


    public void setAppointmentList(LinkedList<String> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public LinkedList<String> getAppointmentList() {
        return appointmentList;
    }

}//Class AppointmentController END
