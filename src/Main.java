import java.util.*;
public class Main {

    private static final String ALPHABET = "абвгдежзийклмнопрстуфхцчшщъыьэюя";

    public static class EncryptResult {
        String detailed;
        String encrypted;
    }

    public static Map<Character, Integer> createAlphabetMap() {
        Map<Character, Integer> map = new LinkedHashMap<>();
        System.out.println("Алфавит:");
        for (int i = 0; i < ALPHABET.length(); i++) {
            map.put(ALPHABET.charAt(i), i + 1);
        }
        return map;
    }

    public static String cleanText(String input) {
        input = input.toLowerCase().replace('ё', 'е');
        return input.replaceAll("[^а-я]", "");
    }

    public static String getKeyWord(Scanner scanner, int textLength) {
        String keyWord;
        while (true) {
            System.out.print("Введите слово-ключ: ");
            keyWord = scanner.nextLine().toLowerCase().replace('ё', 'е');

            if (keyWord.contains(" ")) {
                System.out.println("Ошибка: ключевое слово должно быть одним словом без пробелов.");
                continue;
            }

            keyWord = keyWord.replaceAll("[^а-я]", "");
            if (keyWord.isEmpty()) {
                System.out.println("Ошибка: ключевое слово должно содержать русские буквы.");
                continue;
            }

            if (keyWord.length() >= textLength) {
                System.out.println("Ошибка: длина ключевого слова должна быть меньше длины очищенного текста.");
                continue;
            }
            break;
        }
        return keyWord;
    }

    public static List<String> splitIntoBlocks(String text, int blockSize) {
        List<String> blocks = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < text.length(); i += blockSize) {
            String block;
            if (i + blockSize > text.length()) {
                block = text.substring(i);
                while (block.length() < blockSize) {
                    block += ALPHABET.charAt(random.nextInt(ALPHABET.length()));
                }
            } else {
                block = text.substring(i, i + blockSize);
            }
            blocks.add(block);
        }
        return blocks;
    }

    public static EncryptResult encryptBlock(String block, String key) {
        int len = block.length();
        List<Integer> blockNums = new ArrayList<>();
        List<Integer> keyNums = new ArrayList<>();
        List<Integer> resultNums = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            int blockNum = ALPHABET.indexOf(block.charAt(i)) + 1;
            int keyNum = ALPHABET.indexOf(key.charAt(i)) + 1;
            blockNums.add(blockNum);
            keyNums.add(keyNum);
            int sum = ((blockNum + keyNum - 1) % 32) + 1;
            resultNums.add(sum);
        }

        String blockNumsStr  = blockNums.toString().replace('[', '(').replace(']', ')');
        String keyNumsStr    = keyNums.toString().replace('[', '(').replace(']', ')');
        String resultNumsStr = resultNums.toString().replace('[', '(').replace(']', ')');

        StringBuilder resultLetters = new StringBuilder();
        for (int num : resultNums) {
            resultLetters.append(ALPHABET.charAt(num - 1));
        }

        EncryptResult er = new EncryptResult();
        er.encrypted = resultLetters.toString();
        er.detailed = "(" + block + ") + (" + key + ") = " + blockNumsStr
                + " +_32 " + keyNumsStr + " = " + resultNumsStr
                + " = " + er.encrypted;
        return er;
    }

    public static void main(String[] args) {
        Map<Character, Integer> alphabetMap = createAlphabetMap();
        System.out.println(alphabetMap);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите строку: ");
        String input = scanner.nextLine();

        String cleanedText = cleanText(input);
        int length = cleanedText.length();

        System.out.println("Очищенный текст: " + cleanedText);
        System.out.println("Длина очищенного текста (М): " + length);

        String keyWord = getKeyWord(scanner, length);
        int keyLength = keyWord.length();
        System.out.println("Ключевое слово: " + keyWord);
        System.out.println("Длина ключевого слова: " + keyLength);

        List<String> blocks = splitIntoBlocks(cleanedText, keyLength);
        StringBuilder blocksFormatted = new StringBuilder();
        for (String block : blocks) {
            blocksFormatted.append("(").append(block).append(") ");
        }
        System.out.println("Блоки: " + blocksFormatted.toString().trim());

        StringBuilder finalEncryptedMessage = new StringBuilder();
        System.out.println("\nШифрование по методу Виженера:");
        for (String block : blocks) {
            EncryptResult er = encryptBlock(block, keyWord);
            System.out.println(er.detailed);
            finalEncryptedMessage.append(er.encrypted);
        }
        System.out.println("\nЗашифрованное сообщение: " + finalEncryptedMessage.toString());
    }
}
