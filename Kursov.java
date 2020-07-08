import java.util.*;
class Kursov {
public Kursov (int N){
    this.N=N;
    this.way = new int[N+1];
    this.visited = new boolean[N];
}
    int N;

    // way[] содержит путь продавца
    int[] way = new int[N + 1];

    // visited[] отслеживает уже посещенные узлы на отпределенном пути
    boolean[] visited = new boolean[N];

    // сохранение минимального веса самого короткого пути
    int final_res = Integer.MAX_VALUE;

    // функция для копирования временного решения в окончательное решение
     void copyToFinal(int[] curr_path) {
        for (int i = 0; i < N; i++)
            way[i] = curr_path[i];
        way[N] = curr_path[0];
    }

    // нахождение первой минимальной граничной стоимости, у которой конец в вершине i
     int firstMin(int[][] adj, int i) {
        int min = Integer.MAX_VALUE;
        for (int k = 0; k < N; k++)
            if (adj[i][k] < min && i != k)
                min = adj[i][k];
        return min;
    }

    // нахождение второй минимальной стоимости
     int secondMin(int[][] adj, int i) {
        int first = Integer.MAX_VALUE;
        int second = Integer.MAX_VALUE;

        for (int j = 0; j < N; j++) {
            if (i == j)
                continue;

            if (adj[i][j] <= first) {
                second = first;
                first = adj[i][j];
            } else if (adj[i][j] <= second &&
                    adj[i][j] != first)
                second = adj[i][j];
        }
        return second;
    }


    // curr_bound - нижняя граница корневого узла(текущая граница узла)
    // curr_weight-сохраняет вес пути (текущий вес)
    // level-вершина
    // curr_path[] - хранится решение, которое потом уйдет в final_path
     void TSPRec(int adj[][], int curr_bound, int curr_weight,
                       int level, int curr_path[]) {
        // когда достигли отпределенной вершины( охватили все узлы за один раз)
        if (level == N) {
            // проверяем, есть ли ребро из последней вершины к первой
            if (adj[curr_path[level - 1]][curr_path[0]] != 0) {
                // curr_res- имеет общий вес при решении, которое получили
                int curr_res = curr_weight +
                        adj[curr_path[level - 1]][curr_path[0]];

                // обновление конечного результата, если текущий лучше
                if (curr_res < final_res) {
                    copyToFinal(curr_path);
                    final_res = curr_res;
                }
            }
            return;
        }

        for (int i = 0; i < N; i++) {
            // смотрим на другую вершину, если она не совпадает
            if (adj[curr_path[level - 1]][i] != 0 && visited[i] == false) {
                int temp = curr_bound;
                curr_weight += adj[curr_path[level - 1]][i];

                // другие вычисления для нижней границы. Второй уровень
                if (level == 1)
                    curr_bound -= ((firstMin(adj, curr_path[level - 1]) +
                            firstMin(adj, i)) / 2);
                else
                    curr_bound -= ((secondMin(adj, curr_path[level - 1]) +
                            firstMin(adj, i)) / 2);

                // curr_bound + curr_weight это нижняя граница для узла, на который пришли
                // Если текущяя нижняя граница меньше финального значения, исследуем узел дальше
                if (curr_bound + curr_weight < final_res) {
                    curr_path[level] = i;
                    visited[i] = true;

                    // вызываем TSPRec для следующего уровня
                    TSPRec(adj, curr_bound, curr_weight, level + 1,
                            curr_path);
                }

                // В противном случае обрезаем этот узел, оставляя все изменения в curr_weight и curr_bound
                curr_weight -= adj[curr_path[level - 1]][i];
                curr_bound = temp;

                // так же сбрасываем массив
                Arrays.fill(visited, false);
                for (int j = 0; j <= level - 1; j++)
                    visited[curr_path[j]] = true;
            }
        }
    }

    // Устанавливаем путь
     void TSP(int adj[][]) {
        int curr_path[] = new int[N + 1];

        // Вычесляем начальную нижнюю границу для узла
        int curr_bound = 0;
        Arrays.fill(curr_path, -1);
        Arrays.fill(visited, false);

        for (int i = 0; i < N; i++)
            curr_bound += (firstMin(adj, i) +
                    secondMin(adj, i));

        // округление нижней границы до целого числа
        curr_bound = (curr_bound == 1) ? curr_bound / 2 + 1 :
                curr_bound / 2;

        // Начали с вершины 1,тогда первая вершина равна 0
        visited[0] = true;
        curr_path[0] = 0;

        TSPRec(adj, curr_bound, 0, 1, curr_path);
    }


    public static void main(String[] args) {

        System.out.println("Введите размерность матрицы:");
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Kursov kursov = new Kursov(n);
        System.out.println("Введите поэлементно матрицу через пробел:");
        int adj[][] = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[i][j] = in.nextInt();
            }
        }

        kursov.TSP(adj);

        System.out.printf("Минимальный вес : %d\n", kursov.final_res);
        System.out.printf("Путь: ");
        for (int i = 0; i <= n; i++)
        {
            System.out.printf("%d ", kursov.way[i]+1);
        }
    }
}

