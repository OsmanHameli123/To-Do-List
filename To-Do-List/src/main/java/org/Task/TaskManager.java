package org.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskManager {
    // ===== 2D array storage =====
    private static String[][] tasks;
    private static int taskCount = 0;

    // columns
    private static final int COL_NAME = 0;
    private static final int COL_DESC = 1;
    private static final int COL_PRIORITY = 2;
    private static final int COL_STATUS = 3;
    private static final int COL_CATEGORY = 4;
    private static final int COL_DUE = 5;
    private static final int COL_CREATED = 6;
    private static final int COL_EST_TIME = 7;
    private static final int COL_TAGS = 8;
    private static final int COL_ASSIGNED = 9;

    private static final int COLS = 10;

    private static final String FILE_NAME = "tasks-data.txt";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!loadFromFile()) {
            setupSystem(scanner);
        }

        int choice;
        do {
            printMenu();
            choice = safeInt(scanner, "Select option: ");

            switch (choice) {
                case 1:
                    addTask(scanner);
                    break;
                case 2:
                    showAllTasks();
                    break;
                case 3:
                    changeStatus(scanner);
                    break;
                case 4:
                    filterMenu(scanner);
                    break;
                case 5:
                    sortMenu(scanner);
                    break;
                case 6:
                    editTask(scanner);
                    break;
                case 7:
                    deleteTask(scanner);
                    break;
                case 8:
                    saveToFile();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 9);
    }

    // ===== Setup =====
    private static void setupSystem(Scanner scanner) {
        int capacity = safeInt(scanner, "Max number of tasks (e.g., 50): ");
        if (capacity <= 0) capacity = 50;

        tasks = new String[capacity][COLS];
        taskCount = 0;
        System.out.println("System initialized with capacity: " + capacity);
    }

    // ===== Menu =====
    private static void printMenu() {
        System.out.println("\n=== TO-DO LIST MANAGER (2D ARRAY) ===");
        System.out.println("1. Add new task");
        System.out.println("2. Show all tasks");
        System.out.println("3. Change status (Pending/Done)");
        System.out.println("4. Filter tasks (category/priority/tags)");
        System.out.println("5. Sort tasks (date/priority/estimated time)");
        System.out.println("6. Edit task details");
        System.out.println("7. Delete task");
        System.out.println("8. Save data");
        System.out.println("9. Exit");
    }

    // ===== Core Features =====

    // 1) Add
    private static void addTask(Scanner scanner) {
        ensureCapacity();

        scanner.nextLine(); // consume leftover
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        int priority = safeIntRange(scanner, "Priority (1-5): ", 1, 5);

        System.out.print("Category (Work/Home/Personal...): ");
        scanner.nextLine(); // consume leftover
        String category = scanner.nextLine();

        LocalDate dueDate = safeDate(scanner, "Due date (yyyy-MM-dd): ");

        double estTime = safeDouble(scanner, "Estimated time (hours): ");

        System.out.print("Tags (comma separated, e.g. java,school,urgent): ");
        scanner.nextLine(); // consume leftover
        String tags = scanner.nextLine();

        System.out.print("Assigned to: ");
        String assignedTo = scanner.nextLine();

        // auto fields
        String status = "Pending";
        LocalDate created = LocalDate.now();

        tasks[taskCount][COL_NAME] = name;
        tasks[taskCount][COL_DESC] = desc;
        tasks[taskCount][COL_PRIORITY] = String.valueOf(priority);
        tasks[taskCount][COL_STATUS] = status;
        tasks[taskCount][COL_CATEGORY] = category;
        tasks[taskCount][COL_DUE] = dueDate.format(DATE_FMT);
        tasks[taskCount][COL_CREATED] = created.format(DATE_FMT);
        tasks[taskCount][COL_EST_TIME] = String.valueOf(estTime);
        tasks[taskCount][COL_TAGS] = tags;
        tasks[taskCount][COL_ASSIGNED] = assignedTo;

        taskCount++;
        System.out.println("Task added! Status is 'Pending'.");
    }

    // 2) Show
    private static void showAllTasks() {
        if (taskCount == 0) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.printf("%-5s %-18s %-8s %-10s %-12s %-12s %-12s %-10s %-18s %-12s%n",
                "No", "Name", "Prio", "Status", "Category", "Due", "Created", "Hours", "Tags", "Assigned");

        for (int i = 0; i < taskCount; i++) {
            System.out.printf("%-5d %-18s %-8s %-10s %-12s %-12s %-12s %-10s %-18s %-12s%n",
                    (i + 1),
                    safeStr(tasks[i][COL_NAME]),
                    safeStr(tasks[i][COL_PRIORITY]),
                    safeStr(tasks[i][COL_STATUS]),
                    safeStr(tasks[i][COL_CATEGORY]),
                    safeStr(tasks[i][COL_DUE]),
                    safeStr(tasks[i][COL_CREATED]),
                    safeStr(tasks[i][COL_EST_TIME]),
                    safeStr(tasks[i][COL_TAGS]),
                    safeStr(tasks[i][COL_ASSIGNED])
            );
        }
    }

    // 3) Change status
    private static void changeStatus(Scanner scanner) {
        if (taskCount == 0) {
            System.out.println("No tasks to update.");
            return;
        }

        int index = safeInt(scanner, "Task number to update: ") - 1;
        if (!validIndex(index)) {
            System.out.println("Invalid task number.");
            return;
        }

        scanner.nextLine(); // consume leftover
        System.out.print("New status (Pending/Done): ");
        String status = scanner.nextLine().trim();

        if (!status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Done")) {
            System.out.println("Invalid status. Use 'Pending' or 'Done'.");
            return;
        }

        tasks[index][COL_STATUS] = capitalize(status);
        System.out.println("Status updated!");
    }

    // 4) Filter
    private static void filterMenu(Scanner scanner) {
        if (taskCount == 0) {
            System.out.println("No tasks to filter.");
            return;
        }

        System.out.println("\n--- FILTER ---");
        System.out.println("1. By category");
        System.out.println("2. By priority");
        System.out.println("3. By tag");
        int choice = safeInt(scanner, "Select filter: ");

        scanner.nextLine(); // consume leftover
        switch (choice) {
            case 1: {
                System.out.print("Category: ");
                String cat = scanner.nextLine().trim();
                filterByCategory(cat);
                break;
            }
            case 2: {
                int p = safeIntRange(scanner, "Priority (1-5): ", 1, 5);
                filterByPriority(p);
                break;
            }
            case 3: {
                System.out.print("Tag (one word): ");
                String tag = scanner.nextLine().trim();
                filterByTag(tag);
                break;
            }
            default:
                System.out.println("Invalid filter option.");
        }
    }

    private static void filterByCategory(String category) {
        boolean found = false;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i][COL_CATEGORY] != null &&
                    tasks[i][COL_CATEGORY].equalsIgnoreCase(category)) {
                printOne(i);
                found = true;
            }
        }
        if (!found) System.out.println("No tasks found for category: " + category);
    }

    private static void filterByPriority(int priority) {
        boolean found = false;
        for (int i = 0; i < taskCount; i++) {
            int p = parseIntSafe(tasks[i][COL_PRIORITY], -1);
            if (p == priority) {
                printOne(i);
                found = true;
            }
        }
        if (!found) System.out.println("No tasks found for priority: " + priority);
    }

    private static void filterByTag(String tag) {
        boolean found = false;
        String t = tag.toLowerCase();
        for (int i = 0; i < taskCount; i++) {
            String tags = safeStr(tasks[i][COL_TAGS]).toLowerCase();
            // simple contains; user can keep tags "java,school"
            if (tags.contains(t)) {
                printOne(i);
                found = true;
            }
        }
        if (!found) System.out.println("No tasks found containing tag: " + tag);
    }

    // 5) Sort
    private static void sortMenu(Scanner scanner) {
        if (taskCount == 0) {
            System.out.println("No tasks to sort.");
            return;
        }

        System.out.println("\n--- SORT ---");
        System.out.println("1. By due date");
        System.out.println("2. By priority (high to low)");
        System.out.println("3. By estimated time (low to high)");
        int choice = safeInt(scanner, "Select sort: ");

        switch (choice) {
            case 1:
                sortByDueDate();
                System.out.println("Sorted by due date.");
                break;
            case 2:
                sortByPriorityDesc();
                System.out.println("Sorted by priority (high to low).");
                break;
            case 3:
                sortByEstimatedTimeAsc();
                System.out.println("Sorted by estimated time (low to high).");
                break;
            default:
                System.out.println("Invalid sort option.");
                return;
        }

        showAllTasks();
    }

    private static void sortByDueDate() {
        // selection sort
        for (int i = 0; i < taskCount - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < taskCount; j++) {
                LocalDate d1 = parseDateSafe(tasks[minIdx][COL_DUE]);
                LocalDate d2 = parseDateSafe(tasks[j][COL_DUE]);
                if (d2.isBefore(d1)) {
                    minIdx = j;
                }
            }
            swapRows(i, minIdx);
        }
    }

    private static void sortByPriorityDesc() {
        for (int i = 0; i < taskCount - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < taskCount; j++) {
                int p1 = parseIntSafe(tasks[bestIdx][COL_PRIORITY], 0);
                int p2 = parseIntSafe(tasks[j][COL_PRIORITY], 0);
                if (p2 > p1) {
                    bestIdx = j;
                }
            }
            swapRows(i, bestIdx);
        }
    }

    private static void sortByEstimatedTimeAsc() {
        for (int i = 0; i < taskCount - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < taskCount; j++) {
                double t1 = parseDoubleSafe(tasks[minIdx][COL_EST_TIME], Double.MAX_VALUE);
                double t2 = parseDoubleSafe(tasks[j][COL_EST_TIME], Double.MAX_VALUE);
                if (t2 < t1) {
                    minIdx = j;
                }
            }
            swapRows(i, minIdx);
        }
    }

    // 6) Edit
    private static void editTask(Scanner scanner) {
        if (taskCount == 0) {
            System.out.println("No tasks to edit.");
            return;
        }

        int index = safeInt(scanner, "Task number to edit: ") - 1;
        if (!validIndex(index)) {
            System.out.println("Invalid task number.");
            return;
        }

        System.out.println("\nEditing task #" + (index + 1));
        printOne(index);

        System.out.println("\nFields:");
        System.out.println("1. Name");
        System.out.println("2. Description");
        System.out.println("3. Category");
        System.out.println("4. Due date");
        System.out.println("5. Priority");
        System.out.println("6. Estimated time");
        System.out.println("7. Tags");
        System.out.println("8. Assigned to");
        System.out.println("9. Status");
        int field = safeInt(scanner, "Select field: ");

        scanner.nextLine(); // consume leftover

        switch (field) {
            case 1:
                System.out.print("New name: ");
                tasks[index][COL_NAME] = scanner.nextLine();
                break;
            case 2:
                System.out.print("New description: ");
                tasks[index][COL_DESC] = scanner.nextLine();
                break;
            case 3:
                System.out.print("New category: ");
                tasks[index][COL_CATEGORY] = scanner.nextLine();
                break;
            case 4: {
                LocalDate newDue = safeDate(scanner, "New due date (yyyy-MM-dd): ");
                tasks[index][COL_DUE] = newDue.format(DATE_FMT);
                break;
            }
            case 5: {
                int p = safeIntRange(scanner, "New priority (1-5): ", 1, 5);
                tasks[index][COL_PRIORITY] = String.valueOf(p);
                break;
            }
            case 6: {
                double t = safeDouble(scanner, "New estimated time (hours): ");
                tasks[index][COL_EST_TIME] = String.valueOf(t);
                break;
            }
            case 7:
                System.out.print("New tags: ");
                tasks[index][COL_TAGS] = scanner.nextLine();
                break;
            case 8:
                System.out.print("New assigned to: ");
                tasks[index][COL_ASSIGNED] = scanner.nextLine();
                break;
            case 9:
                System.out.print("New status (Pending/Done): ");
                String s = scanner.nextLine().trim();
                if (!s.equalsIgnoreCase("Pending") && !s.equalsIgnoreCase("Done")) {
                    System.out.println("Invalid status.");
                    return;
                }
                tasks[index][COL_STATUS] = capitalize(s);
                break;
            default:
                System.out.println("Invalid field.");
                return;
        }

        System.out.println("Task updated!");
    }

    // 7) Delete
    private static void deleteTask(Scanner scanner) {
        if (taskCount == 0) {
            System.out.println("No tasks to delete.");
            return;
        }

        int index = safeInt(scanner, "Task number to delete: ") - 1;
        if (!validIndex(index)) {
            System.out.println("Invalid task number.");
            return;
        }

        // shift up
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }

        // clear last row reference
        tasks[taskCount - 1] = new String[COLS];
        taskCount--;

        System.out.println("Task deleted!");
    }

    // 8) Save / Load
    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            // first line: taskCount and capacity
            writer.write(taskCount + " " + tasks.length + "\n");

            // each task row in 1 line: 10 columns separated by TAB
            for (int i = 0; i < taskCount; i++) {
                for (int c = 0; c < COLS; c++) {
                    String cell = tasks[i][c];
                    writer.write(escape(cell));
                    if (c < COLS - 1) writer.write("\t");
                }
                writer.write("\n");
            }

            System.out.println("Data saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private static boolean loadFromFile() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return false;

            Scanner scanner = new Scanner(file);
            int loadedCount = scanner.nextInt();
            int capacity = scanner.nextInt();
            scanner.nextLine();

            if (capacity <= 0) capacity = 50;
            tasks = new String[capacity][COLS];
            taskCount = 0;

            for (int i = 0; i < loadedCount; i++) {
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine();
                String[] parts = line.split("\t", -1);

                // if corrupted line, skip
                if (parts.length != COLS) continue;

                for (int c = 0; c < COLS; c++) {
                    tasks[taskCount][c] = unescape(parts[c]);
                }
                taskCount++;
            }

            scanner.close();
            System.out.println("Data loaded from " + FILE_NAME + " (tasks: " + taskCount + ")");
            return true;
        } catch (Exception e) {
            // if anything goes wrong, just start fresh
            return false;
        }
    }

    // ===== Helpers =====

    private static void printOne(int i) {
        System.out.println("--------------------------------------------------");
        System.out.println("Task #" + (i + 1));
        System.out.println("Name: " + safeStr(tasks[i][COL_NAME]));
        System.out.println("Description: " + safeStr(tasks[i][COL_DESC]));
        System.out.println("Priority: " + safeStr(tasks[i][COL_PRIORITY]));
        System.out.println("Status: " + safeStr(tasks[i][COL_STATUS]));
        System.out.println("Category: " + safeStr(tasks[i][COL_CATEGORY]));
        System.out.println("Due Date: " + safeStr(tasks[i][COL_DUE]));
        System.out.println("Creation Date: " + safeStr(tasks[i][COL_CREATED]));
        System.out.println("Estimated Time: " + safeStr(tasks[i][COL_EST_TIME]) + " h");
        System.out.println("Tags: " + safeStr(tasks[i][COL_TAGS]));
        System.out.println("Assigned To: " + safeStr(tasks[i][COL_ASSIGNED]));
        System.out.println("--------------------------------------------------");
    }

    private static boolean validIndex(int idx) {
        return idx >= 0 && idx < taskCount;
    }

    private static void ensureCapacity() {
        if (tasks == null) {
            tasks = new String[50][COLS];
            return;
        }
        if (taskCount < tasks.length) return;

        // expand by +20
        int newCap = tasks.length + 20;
        String[][] newArr = new String[newCap][COLS];
        for (int i = 0; i < tasks.length; i++) {
            newArr[i] = tasks[i];
        }
        tasks = newArr;
        System.out.println("Capacity increased to: " + newCap);
    }

    private static void swapRows(int i, int j) {
        if (i == j) return;
        String[] tmp = tasks[i];
        tasks[i] = tasks[j];
        tasks[j] = tmp;
    }

    private static int safeInt(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number. Try again.");
                scanner.nextLine();
            }
        }
    }

    private static int safeIntRange(Scanner scanner, String message, int min, int max) {
        while (true) {
            int x = safeInt(scanner, message);
            if (x >= min && x <= max) return x;
            System.out.println("Value must be between " + min + " and " + max);
        }
    }

    private static double safeDouble(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number. Try again.");
                scanner.nextLine();
            }
        }
    }

    private static LocalDate safeDate(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                String s = scanner.next().trim();
                return LocalDate.parse(s, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd (example: 2025-12-31)");
            }
        }
    }

    private static LocalDate parseDateSafe(String s) {
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            // if missing/bad date, push it far
            return LocalDate.of(9999, 12, 31);
        }
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    private static double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return def; }
    }

    private static String safeStr(String s) {
        return (s == null) ? "" : s;
    }

    // File-safe encoding for tabs/newlines/backslashes
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        // important order: unescape \\ last? Here we do simple reverse for our patterns
        return s.replace("\\t", "\t")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String lower = s.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}