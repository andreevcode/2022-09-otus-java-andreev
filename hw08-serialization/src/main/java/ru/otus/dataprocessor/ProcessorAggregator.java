package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

public class ProcessorAggregator implements Processor {
    @Override
    public Map<String, Double> process(List<Measurement> data) {
        return data.stream()
                .sorted(Comparator.comparing(Measurement::getName))
                .collect(groupingBy(
                                Measurement::getName,
                                TreeMap::new,
                                reducing(
                                        0.0,
                                        Measurement::getValue,
                                        Double::sum)
                        )
                );
    }
}
