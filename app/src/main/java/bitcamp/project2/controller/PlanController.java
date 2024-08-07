package bitcamp.project2.controller;

import bitcamp.project2.util.Prompt;
import bitcamp.project2.vo.Plan;
import bitcamp.project2.vo.User;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bitcamp.project2.util.Prompt.*;

public class PlanController{
//    UserController uc = UserController.getInstance();
//    User loginUser = uc.getUserByName(Membership.getInstance().getName());
//    LinkedList<Plan> planList = loginUser.getPlanList();
      LinkedList<Plan> planList;

    ///////////////////////////////////////////////////////////
    ////////////////////// getInstance() //////////////////////
    ///////////////////////////////////////////////////////////
    private static PlanController m;

    // setup Menu Instance
    public static PlanController getInstance(User user) {

        m = new PlanController(user);

        return m;
    }// Method getInstance END

    // reset PlanController Instance
    public static void freeInstance() {
        m = null;
    }// Method freeInstance END


    PlanController(User user){
        this.planList = user.getPlanList();
    }



    ///////////////////////////////////////////////////////////
    ///////////////////////// Method //////////////////////////
    ///////////////////////////////////////////////////////////
    public void plan(String subMenuNo) {
        switch(subMenuNo) {
            case "1" :
                addPlan();
                break;
            case "2" :
                updatePlan();
                break;
            case "3" :
                deletePlan();
                break;
//            case "4" :
//                addTestData();
//                break;
            default:
                printNumberLimitException();
                break;
        }
    }//Method plan END




    public void listPlan() {
//        String line = printLine();

        if(!planList.isEmpty()) {
            sortPlan();
            System.out.print(printLine());
            System.out.println("No\t\tTitle\t\t\t\tDate");
            for(int i = 0; i < planList.size(); i++) {
                printlistPlanDate(i);
            }
            System.out.print(printLine()+"\n");
        }
    }//Method listPlan END




    private void printlistPlanDate(int i){
        String date = "";
        Plan plan = planList.get(i);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
        String startDate = formatter.format(plan.getStartDate());
        String endDate = formatter.format(plan.getEndDate());

        if(startDate.equals(endDate)) {
            date = startDate;
        } else {
            date = startDate + " ~ " + endDate;
        }

        if(plan.getRepeatedDays() != null) {
            date = date + " " + plan.getRepeatedDays();
        }

        System.out.printf("%d.\t\t%s%s%s\n", (i + 1), plan.getTitle(), getTabByString(plan.getTitle()), date);
    }//Method printlistPlanDate END





    private void addPlan() {
        Plan plan = new Plan();
        plan.setTitle(Prompt.input("제목? "));

        setDates(plan);

        planList.add(plan);
        System.out.println("등록되었습니다.\n");
        loading(1000);
    }//Method addPlan END




    private void updatePlan() {
        if(isValidatePlanList()) {
            int planNo = Prompt.inputInt("수정할 일정?");

            if (isValidatePlan(planNo)) {
                Plan plan = planList.get(planNo - 1);
                System.out.print("[1] 제목\t[2] 기간\n");

                int command = Prompt.inputInt("수정할 항목?");
                switch (command){
                    case 1:
                        plan.setTitle(Prompt.input("'%s' 이름 변경 : ", plan.getTitle()));
                        System.out.println("수정되었습니다.\n");
                        loading(1000);
                        break;
                    case 2:
                        setDates(plan);
                        System.out.println("수정되었습니다.\n");
                        loading(1000);
                        break;
                    default:
                        System.out.println("잘못된 항목입니다.");
                        loading(1000);
                        break;
                }
//                if (command == 1) {
//                    plan.setTitle(Prompt.input("'%s' 이름 변경 : ", plan.getTitle()));
//                    System.out.println("수정되었습니다.\n");
//
//                } else if (command == 2) {
//                    setDates(plan);
//
//                    System.out.println("수정되었습니다.\n");
//                } else {
//                    System.out.println("잘못된 항목입니다.");
//                }
            }//Method isValidatePlan END
        }//Method isValidatePlanList END

    }//Method updatePlan END






    private void deletePlan() {
       if(isValidatePlanList()) {
           int planNo = Prompt.inputInt("삭제할 일정?");
           if (isValidatePlan(planNo)) {
               Plan plan = planList.get(planNo - 1);

               String command = Prompt.input("%s 일정을 삭제하시겠습니까?(y/n)", plan.getTitle());
               if (command.equals("Y") || command.equals("y")) {
                   planList.remove(planNo - 1);
                   System.out.printf("'%s' 일정을 삭제했습니다.\n\n", plan.getTitle());
                   loading(1000);
               }
           }//Method isValidatePlan END
       }//Method isValidatePlanList END

    }//Method deletePlan END






    public static String getTabByString(String str) {
        int count = 0;
        int len = str.length();
        Pattern pattern = Pattern.compile("[가-힣]");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            count++;
        }
        return "\t".repeat(((20 - count - len + 3) / 4));
    }//Method getTabByString END





    class Term {
        String startDate;
        String endDate;
        String repeatedDays;
    }

    private void setDates(Plan plan) {
        int month = Prompt.inputInt("월? ");
        if(isValidateDates(month)) {

            int lastDay = printCalendar(2024, month);
            String days = Prompt.input("일?(1-%d 반복할요일)", lastDay);

            Term term = splitDate(month, days);

            plan.setStartDate(Date.valueOf(term.startDate));
            plan.setEndDate(Date.valueOf(term.endDate));
            plan.setRepeatedDays(term.repeatedDays);


//        if(month < 1 || month > 12) {
//            System.out.printf("%d월은 없는걸...?\n", month);
//            return;
//        }


//            String startDate;
//            String endDate;
//            String repeatedDays = null;

//            if (days.contains(" ")) {
//                repeatedDays = days.split(" ")[1];
//            }
//
//            if (days.contains("-")) {
//                startDate = String.format("2024-%d-%s", month, days.split("-")[0]);
//                endDate = String.format("2024-%d-%s", month, days.split("-")[1].split(" ")[0]);
//            } else {
//                startDate = endDate = String.format("2024-%d-%s", month, days.split(" ")[0]);
//            }

        }
    }//Method setDates END


    private Term splitDate(int month, String days){
        Term term = new Term();

        if (days.contains(" ")) {
            term.repeatedDays = days.split(" ")[1];
        }

        if (days.contains("-")) {
            term.startDate = String.format("2024-%d-%s", month, days.split("-")[0]);
            term.endDate = String.format("2024-%d-%s", month, days.split("-")[1].split(" ")[0]);
        } else {
            term.startDate = term.endDate = String.format("2024-%d-%s", month, days.split(" ")[0]);
        }

        return term;
    }//Method setDates END





    private boolean isValidateDates(int month){
        if(month < 1 || month > 12) {
            System.out.printf("%d월은 없는걸...?\n", month);
            return false;
        }
        return true;
    }//Method isValidateDates END




    private boolean isValidatePlanList(){
        if(planList.isEmpty()) {
            System.out.println("현재 등록된 일정이 없습니다.\n");
            loading(1000);
            return false;
        }
        return true;
    }//Method isValidatePlanList END






    private boolean isValidatePlan(int planNo) {
        if(planNo < 1 || planNo > planList.size()) {
            System.out.println("없는 일정입니다.\n");
            loading(1000);
            return false;
        } else {
            return true;
        }
    }//Method isValidatePlan END





    private void sortPlan() {
        for (int i = 0; i < planList.size() - 1; i++) {
            for(int j = i + 1; j < planList.size(); j++) {
                Date date1 = (Date) planList.get(i).getStartDate();
                Date date2 = (Date) planList.get(j).getStartDate();
                if(date1.after(date2)) {
                    Plan temp = planList.get(i);
                    planList.set(i, planList.get(j));
                    planList.set(j, temp);
                }
            }
        }
    }

//    private void addTestData() {
//        planList.add(new Plan(1, "가족모임", Date.valueOf("2024-07-01"), Date.valueOf("2024-07-10"), null));
//        planList.add(new Plan(2, "민지랑 저녁식사", Date.valueOf("2024-07-11"), Date.valueOf("2024-07-11"), null));
//        planList.add(new Plan(3, "회식", Date.valueOf("2024-07-05"), Date.valueOf("2024-07-06"), null));
//        planList.add(new Plan(4, "일", Date.valueOf("2024-07-05"), Date.valueOf("2024-07-06"), "월화"));
//        System.out.println("테스트데이터 등록\n");
//    }

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


    public LinkedList<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(LinkedList<Plan> planList) {
        this.planList = planList;
    }
}
