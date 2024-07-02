/*
 * This source file was generated by the Gradle 'init' task
 */
package bitcamp.project2;

import bitcamp.project2.util.Membership;
import bitcamp.project2.controller.PlanController;
import bitcamp.project2.controller.UserController;
import bitcamp.project2.util.Prompt;

public class App {
    static final int ans = 3;

    String[] mainMenus = new String[]{"내 일정", "약속추가", "사용자관리", "종료"};
    String[][] subMenus = {
        {"등록", "수정", "삭제"},
        {},
        {"수정", "삭제"}
    };

    UserController userController = new UserController();
    PlanController planController = new PlanController();

    public static void main(String[] args) {

        Membership m = Membership.getInstance();
        m.menu();

        new App().execute();
    }

    void execute() {
        // 내 일정
        if(ans == 1) {
            String menuTitle = "내 일정";
            System.out.println("[내 일정]");

            while (true) {
                planController.listPlan();

                String[] menus = subMenus[ans - 1];
                for (int i = 0; i < menus.length; i++) {
                    System.out.printf("[%d] %s\t", (i + 1), menus[i]);
                }
                System.out.println("[0] 이전");

                String command = Prompt.input(String.format("메인/%s>", menuTitle));
                if(command.equals("0")) {
                    break;
                }

                planController.executePlanCommand(command);
            }
        }

        // 사용자 관리
        if(ans == 3) {
            if (!Prompt.input("관리자 비밀번호 : ").equals("0000")) {
                System.out.println("비밀번호가 잘못되었습니다.");
            } else {
                System.out.println("[사용자관리 화면에 접속합니다.]");
                while (true) {
                    userController.listUser();
                    String menuTitle = "사용자관리";

                    String[] menus = subMenus[ans - 1];
                    for (int i = 0; i < menus.length; i++) {
                        System.out.printf("[%d] %s\t", (i + 1), menus[i]);
                    }
                    System.out.println("[0] 이전");

                    String command = Prompt.input(String.format("메인/%s>", menuTitle));
                    if (command.equals("0")) {
                        break;
                    }

                    userController.executeUserCommand(command);
                }
            }
        }
    }
}
