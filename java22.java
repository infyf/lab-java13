import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MatrixAddition extends RecursiveTask<int[][]> {
    private final int[][] matrix1;
    private final int[][] matrix2;
    private final int rowStart;
    private final int colStart;
    private final int size;

    public MatrixAddition(int[][] matrix1, int[][] matrix2, int rowStart, int colStart, int size) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.rowStart = rowStart;
        this.colStart = colStart;
        this.size = size;
    }

    @Override
    protected int[][] compute() {
        if (size <= 1000) { // ���� ����� ������� ������ �����, ���������� ��������� ������
            int[][] result = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    result[i][j] = matrix1[rowStart + i][colStart + j] + matrix2[rowStart + i][colStart + j];
                }
            }
            return result;
        } else {
            // ���������� ����� ������� �� ����� �������
            int newSize = size / 2;
            MatrixAddition topLeft = new MatrixAddition(matrix1, matrix2, rowStart, colStart, newSize);
            MatrixAddition topRight = new MatrixAddition(matrix1, matrix2, rowStart, colStart + newSize, newSize);
            MatrixAddition bottomLeft = new MatrixAddition(matrix1, matrix2, rowStart + newSize, colStart, newSize);
            MatrixAddition bottomRight = new MatrixAddition(matrix1, matrix2, rowStart + newSize, colStart + newSize, newSize);

            // ��������� �������� ����������
            topLeft.fork();
            topRight.fork();
            bottomLeft.fork();
            int[][] resultBottomRight = bottomRight.compute(); // bottomRight ���������� � ��������� ������

            // ������� ���������� ����� ������� � �������� �� ����������
            int[][] resultTopLeft = topLeft.join();
            int[][] resultTopRight = topRight.join();
            int[][] resultBottomLeft = bottomLeft.join();

            // ������� ���������� � ������ �������
            int[][] result = new int[size][size];
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    result[i][j] = resultTopLeft[i][j];
                    result[i][j + newSize] = resultTopRight[i][j];
                    result[i + newSize][j] = resultBottomLeft[i][j];
                }
            }
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    result[i + newSize][j + newSize] = resultBottomRight[i][j];
                }
            }
            return result;
        }
    }

    public static void main(String[] args) {
        int size = 20000;
        int[][] matrix1 = new int[size][size];
        int[][] matrix2 = new int[size][size];
        // ���������� ������� ������ (�������� ������ ��� ��������)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix1[i][j] = i + j;
                matrix2[i][j] = i - j;
            }
        }

        // ��������� ForkJoinPool � ������ ��� ��������� �������
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MatrixAddition task = new MatrixAddition(matrix1, matrix2, 0, 0, size);

        // ��������� ������ �� �������� ���������
        int[][] result = forkJoinPool.invoke(task);

        // �������� ���������
        // �� ������ ��� ��������, ����� ������� �� ������� ������� ������� ��� �������� � ����, ���� ������� ������� ������
        System.out.println("��������� ��������� �������:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }
}
