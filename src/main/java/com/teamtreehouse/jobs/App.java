package com.teamtreehouse.jobs;

import com.teamtreehouse.jobs.model.Job;
import com.teamtreehouse.jobs.service.JobService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {
        JobService service = new JobService();
        boolean shouldRefresh = false;
        try {
            if (shouldRefresh) {
                service.refresh();
            }
            List<Job> jobs = service.loadJobs();
            System.out.printf("Total jobs:  %d %n %n", jobs.size());
            explore(jobs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void explore(List<Job> jobs) {

        Function<String, String> indeedDateStringConverter = createCustomDateConverterFunction(DateTimeFormatter.RFC_1123_DATE_TIME,
                DateTimeFormatter.ofPattern("MM-dd-YYYY"));
        jobs.stream()
                .map(Job::getDateTimeString)
                .map(indeedDateStringConverter)
                .limit(5)
                .forEach(System.out::println);
    }

    private static Function<String, String> createCustomDateConverterFunction(DateTimeFormatter in,
                                                                              DateTimeFormatter out) {
        return dateString -> {
            return LocalDateTime.parse(dateString, in).format(out);
        };

        // OR
        // Lambda Expression
        // return dateString -> LocalDateTime.parse(dateString, in).format((out));
    }

    private static void printDateStringInDifferentFormat(List<Job> jobs) {
        Function<String, LocalDateTime> localDateTimeParser =
                dateString -> LocalDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME);

        Function<LocalDateTime, String> siteDateStringConverter =
                dateTime -> dateTime.format(DateTimeFormatter.ofPattern("M / dd / YY"));

        Function<String, String>  indeedDateStringConverter = localDateTimeParser.andThen(siteDateStringConverter);

        jobs.stream()
                .map(Job::getDateTimeString)
                .map(indeedDateStringConverter)
                .limit(5)
                .forEach(System.out::println);
    }

    private static void distinctCompaniesInSortedOrder(List<Job> jobs) {
        List<String> companies = jobs.stream()
                .map(Job::getCompany)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.printf("Total distinct jobs:%d", companies.size());
    }

    private static void higherOrderFunctions(List<Job> jobs) {
        Job firstJob = jobs.get(0);
        emailMatchingJob(firstJob, job -> "CA".equals(job.getState())); // predicate

        Predicate<Job> inStateCA = job -> "NY".equals(job.getState());
        Job jobInCA = jobs.stream()
                .filter(inStateCA.and(App::isJuniorJob)).findFirst() // functional composition
                .orElseThrow(NullPointerException::new);

        emailMatchingJob(jobInCA, inStateCA);
    }

    public static void emailMatchingJob(Job job, Predicate<Job> checker) {
        if (checker.test(job)) {
            System.out.printf("Sending an email about %s%n", job);
        } else {
            System.err.printf("Not matched the criteria.%n");
        }
    }

    private static void printFilteredCompaniesAndDebug(List<String> companies) {
        companies.stream()
                .peek(company -> System.out.printf("=========> %s.%n", company))
                .filter(company -> company.startsWith("N"))
                .forEach(System.out::println);
    }

    private static void rangeWithStepping(List<String> companies) {
        int pageSize = 20;
        int noOfPages = companies.size() / pageSize;
        IntStream.iterate(1, i -> i + pageSize)
                .mapToObj(i -> String.format("%d. %s", i, companies.get(i)))
                .limit(noOfPages)
                .forEach(System.out::println);
    }

    private static void displayCompaniesMenuWithRange(List<String> companies) {
        IntStream.rangeClosed(1, 20)
                .mapToObj(i -> String.format("%d. %s", i, companies.get(i - 1)))
                .forEach(System.out::println);
    }

    private static void displayCompaniesMenuImperatively(List<String> companies) {
        for (int i = 0; i < 20; i++) {
            System.out.printf("%d. %s%n", i + 1, companies.get(i));
        }
    }

    private static void searchAndPrint(List<Job> jobs, String searchTerm) {
        Optional<Job> searchResult = luckyJobSearch(jobs, searchTerm);
        System.out.println(searchResult.map(Job::getTitle).orElse("No jobs found."));
    }

    private static Optional<Job> luckyJobSearch(List<Job> jobs, String searchTerm) {
        return jobs.stream()
                .filter(job -> job.getTitle().contains(searchTerm))
                .findFirst();
    }

    private static Optional<String> getLongestCompanyName(List<Job> jobs) {
        return jobs.stream()
                .map(Job::getCompany)
                .max(Comparator.comparingInt(String::length));
    }


    private static OptionalDouble getAvgLengthOfCompanyName(List<Job> jobs) {
        return jobs.stream()
                .map(Job::getCompany)
                .mapToInt(String::length)
                .average();
    }

    private static Map<String, Long> getWordCloudWithStream(List<Job> jobs) {
        return jobs.stream()
                .map(Job::getSnippet)
                .map(snippet -> snippet.split("\\W+"))
                .flatMap(Stream::of)
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

    }

    private static Map<String, Long> getWordCloudImperatively(List<Job> jobs) {
        final Map<String, Long> wordMap = new HashMap<>();

        String snippet;
        for (Job job : jobs) {
            snippet = job.getSnippet();
            String[] words = snippet.split(("\\W+"));
            for (String word : words) {
                if (word.isEmpty()) continue;
                word = word.toLowerCase();
                Long count = wordMap.get(word);
                if (count == null) {
                    count = 0L;
                }
                wordMap.put(word, ++count);

            }

        }

        return wordMap;
    }

    private static List<String> getThreeJuniorJobsCaptionsStream(List<Job> jobs) {
        return jobs.stream()
                .filter(App::isJuniorJob)
                .map(Job::getCaption)
                .limit(3)
                .collect(Collectors.toList());
    }

    private static List<String> getThreeJuniorJobsCaptionsImperatively(List<Job> jobs) {
        List<String> list = new ArrayList<>();
        for (Job job : jobs) {
            if (isJuniorJob(job)) {
                list.add(job.getCaption());
                if (list.size() == 3) {
                    break;
                }
            }
        }
        return list;
    }

    private static List<Job> getThreeJuniorJobsUsingStreams(List<Job> jobs) {
        return jobs.stream()
                .filter(App::isJuniorJob)
                .limit(3)
                .collect(Collectors.toList());
    }

    private static List<Job> getThreeJuniorJobsImperatively(List<Job> jobs) {
        List<Job> juniorJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (isJuniorJob(job)) {
                juniorJobs.add(job);
                if (juniorJobs.size() == 3) {
                    break;
                }
            }
        }
        return juniorJobs;
    }

    private static boolean isJuniorJob(Job job) {
        return job.getTitle().contains("Junior") || job.getTitle().contains("Jr");
    }


    private static void printJobsWithStream(List<Job> jobs, String state, String city) {
        jobs.stream().parallel()
                .filter(job -> job.getState().equals(state))
                .filter(job -> job.getCity().equals(city))
                .forEach(System.out::println);
    }

    private static void printJobsImperatively(List<Job> jobs, String state, String city) {
        for (Job job : jobs) {
            if (job.getState().equals(state) && job.getCity().equals(city)) {
                System.out.println(job);
            }
        }
    }
}
