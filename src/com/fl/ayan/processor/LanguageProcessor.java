package com.fl.ayan.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LanguageProcessor {

    public static final String MYSTERY_TEXT_FILE = "mystery.txt";

    /**
     * @param localDirectory : local folder
     * @param n : value of n
     * @return Model
     */
    public Map<String, Integer> formNGramModelForMysteryText(
                    final File localDirectory, final int n) {
        Map<String, Integer> map = null;
        final File mysteryTxtFile =
                new File(localDirectory.getAbsolutePath()+"/"+MYSTERY_TEXT_FILE);
        try {
            map = formNGramModelFromFile(mysteryTxtFile, n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(map);
        return map;
    }

    /**
     * @param directories : list of all sub-folders
     * @param n : value of n
     * @return Map<String, Map<String, Integer>>
     */
    public Map<String, Map<String, Integer>> formNGramModelsForAllLanguages(
            final List<File> directories, final int n) {

        final Map<String, Map<String, Integer>> modelMap = new HashMap<>();
        directories.parallelStream().forEach( directory -> {
            List<Map<String, Integer>> modelsForLangDir = new ArrayList<>();
            Arrays.stream(Objects.requireNonNull(directory.listFiles(File::isFile))).forEach(
                    file -> {
                        if(file.getName().contains(".txt")) {
                            try {
                                modelsForLangDir.add(formNGramModelFromFile(file, n));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            final Optional<Map<String, Integer>> reduce = modelsForLangDir.stream().reduce((firstMap, secondMap) -> Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            Integer::sum)));
            modelMap.put(directory.getName(), reduce.orElse(null));
        });
        System.out.println(modelMap);
        return modelMap;
    }

    /**
     * @param mapOfModels : list of language models
     * @param mysteryTextModel : mystery model
     * @return language
     */
    public String detectClosestLanguage(
            final Map<String, Map<String, Integer>> mapOfModels,
                final Map<String, Integer> mysteryTextModel) {
        Map<String, Double> docDistAngleMaps = new HashMap<>();
        AtomicInteger bSquared = new AtomicInteger();
        mysteryTextModel.values().iterator().forEachRemaining(
                modelValue -> {
                    bSquared.addAndGet(modelValue * modelValue);
                }
        );
        mapOfModels.entrySet().forEach(
                entry -> {
                    final Map<String, Integer> model = entry.getValue();
                    final String language = entry.getKey();
                    AtomicInteger dotProduct = new AtomicInteger(Integer.MAX_VALUE);
                    AtomicInteger aSquared = new AtomicInteger();
                    model.values().iterator().forEachRemaining(
                            modelValue -> {
                                aSquared.addAndGet(modelValue * modelValue);
                        }
                    );
                    model.entrySet().forEach(
                            modelEntry -> {
                                final String nGram = modelEntry.getKey();
                                final int a = modelEntry.getValue();
                                if(mysteryTextModel.containsKey(nGram)){
                                    final int b = mysteryTextModel.get(nGram);
                                    dotProduct.compareAndExchange(Integer.MAX_VALUE, 0);
                                    dotProduct.getAndAdd(a * b);
                                }
                            }
                    );
                    final double docDist = dotProduct.doubleValue() /
                            (Math.sqrt(aSquared.doubleValue()) * Math.sqrt(bSquared.doubleValue()));
                    System.out.println(language+ ": "+ docDist);
                    final double docDistAngle = Math.toDegrees(Math.acos(docDist));
                    System.out.println("docDistAngle: "+docDistAngle);
                    docDistAngleMaps.put(language, docDistAngle);
                }
        );

        System.out.println(docDistAngleMaps);
        return docDistAngleMaps.entrySet().stream()
                .filter(entry -> !Double.isNaN(entry.getValue()))
                .min(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
    }

    /**
     * @param file : file to generate model
     * @param n : value of n
     * @return Map<String, Integer>
     */
    private Map<String, Integer> formNGramModelFromFile(
            final File file, final int n) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        content = content.replaceAll(",", "")
            .replaceAll("[.]", "")
            .replaceAll("[ ]", "")
            .replaceAll("[-]", "");
        return ngrams(n, content.toLowerCase());
    }

    /**
     * @param n : n of ngrams
     * @param str : String
     */
    private Map<String, Integer> ngrams(int n, String str) {
        final Map<String, Integer> ngrams = new HashMap<>();
        IntStream.range(0, str.length() - n + 1)
                .forEach( i -> {
                    final String subs = str.substring(i, i + n);
                    int frequency = ngrams.containsKey(subs)?(ngrams.get(subs)): 0;
                    ngrams.put(subs, ++frequency);
                });
        return ngrams;
    }
}
