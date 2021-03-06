package controller;

import racingcar.Car;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.joining;

/**
 * 레이싱게임의 로직을 담당하는 클래스
 */
public class GameController {
    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    private static final int MAX_NAME_LENGTH = 5;
    private static final int MIN_COUNT = 1;

    private static int status;
    private static int count;
    private static int maxDistance = 1;
    private static List<Car> cars = new ArrayList<>();

    public static void start(Scanner scanner) {
        setCarNames(scanner); //자동차 이름을 입력받음
        setCount(scanner); //시도할 횟수를 입력받음
        printResult(); //결과를 출력
    }

    /**
     * 자동차의 이름을 등록하는 메소드
     *
     * @param scanner
     */
    public static void setCarNames(Scanner scanner) {
        List<String> names;
        do {
            status = SUCCESS;
            System.out.println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)");
            names = Arrays.stream(scanner.nextLine().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            status = validateName(names);
        } while (status == FAIL);
        names.forEach(name -> cars.add(new Car(name)));

    }

    /**
     * 입력받은 값이 정상인지 검증하는 메소드
     *
     * @param names
     */
    public static int validateName(List<String> names) {
        if (names.isEmpty()) {
            System.out.println("[ERROR] 자동차 이름으로 콤마를 입력했습니다. 다시 입력하세요.");
            return FAIL;
        } else if (checkDuplicateName(names)) {
            System.out.println("[ERROR] 중복된 자동차 이름이 있습니다.");
            return FAIL;
        }
        for (String name : names) {
            if (name.isBlank()) {
                System.out.println(name + "[ERROR] 자동차 이름으로 빈 문자열을 사용할 수 없습니다.");
                return FAIL;
            } else if (name.length() > MAX_NAME_LENGTH) {
                System.out.println("[ERROR] 자동차 이름은 1~5 글자만 가능합니다.");
                return FAIL;
            }
        }
        return SUCCESS;
    }

    /**
     * 자동차 이름의 중복 여부를 체크하는 메소드
     *
     * @param names
     * @return
     */
    public static boolean checkDuplicateName(List<String> names) {
        Set<String> carSet = new HashSet<>();

        for (int i = 0; i < names.size(); i++) {
            carSet.add(names.get(i));
        }
        if (names.size() == carSet.size()) return false;
        return true;
    }

    /**
     * 게임을 시도할 횟수를 입력받는 메소드
     *
     * @param scanner
     */
    public static void setCount(Scanner scanner) {
        do {
            status = SUCCESS;
            System.out.println("시도할 횟수는 몇 회인가요?");
            try {
                count = scanner.nextInt();
                if (count < MIN_COUNT) {
                    System.out.println("[ERROR] 시도 횟수는 1 이상이어야 합니다. 다시 입력하세요.");
                    status = FAIL;
                }
            } catch (InputMismatchException e) {
                System.out.println("[ERROR] 시도 횟수는 숫자여야 합니다.");
                status = FAIL;
                scanner.next();
            }
        } while (status == FAIL);
    }

    /**
     * 각 자동차의 이동거리를 출력하는 메소드
     */
    public static void printDistance() {
        for (Car car : cars) {
            if (car.getPosition() > maxDistance) {
                maxDistance = car.getPosition();
            }
            System.out.println(car.getName() + " : " + "-".repeat(car.getPosition()));
        }
    }

    /**
     * 입력이 끝난 후 결과를 출력하는 메소드
     */
    public static void printResult() {
        System.out.println("실행 결과");
        for (int i = 0; i < count; i++) {
            cars.forEach(Car::moveCar);
            printDistance();
            System.out.println();
        }
        printWinner();
    }

    /**
     * 우승자를 출력하는 메소드
     */
    public static void printWinner() {
        String candidates = cars.stream()
                .filter(car -> car.isMaxPosition(maxDistance))
                .map(Car::getName)
                .collect(joining(", "));
        if (checkWinner(candidates)) System.out.println("최종 우승자: " + candidates);
    }

    /**
     * 우승자 여부를 반환하는 메소드
     *
     * @param candidates
     * @return
     */
    public static boolean checkWinner(String candidates) {
        if (!candidates.isEmpty()) return true;
        System.out.println("모든 차량이 이동하지 않아서 우승자가 없습니다.");
        return false;
    }
}
