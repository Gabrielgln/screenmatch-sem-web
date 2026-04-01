package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.domain.Episodio;
import br.com.alura.screenmatch.domain.EpisodioDTO;
import br.com.alura.screenmatch.domain.SerieDTO;
import br.com.alura.screenmatch.domain.TemporadaDTO;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * flatMap: transforma uma lista em todas as listas juntas.
 * toList: gera uma lista imutável (não pode fazer operações nessa lista).
 * peek: Exibi as etapas de uma stream.
 */
public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private static final String ENDERECO = "https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=402e2999";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados(new ObjectMapper());

    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        SerieDTO serieDTO = converteDados.obterDados(json, SerieDTO.class);
        System.out.println(serieDTO);

        List<TemporadaDTO> temporadas = new ArrayList<>();
        for (int i = 1; i <= serieDTO.totalTemporadas(); i++){
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            TemporadaDTO temporadaDTO = converteDados.obterDados(json, TemporadaDTO.class);
            temporadas.add(temporadaDTO);
        }

        temporadas.forEach(System.out::println);

        temporadas.stream()
                .flatMap(x -> x.episodios().stream())
                .map(EpisodioDTO::titulo)
                .forEach(System.out::println);

//        List<EpisodioDTO> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .toList();
//
//        System.out.println("\nTop 5 episódios");
//
//        episodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
//                .sorted(Comparator.comparing(EpisodioDTO::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação " + e))
//                .limit(5)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);

        System.out.println("\nTop 5 episódios personalizada");

        List<Episodio> episodioList = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                )
                .toList();

        episodioList.forEach(System.out::println);

        System.out.println("Digite um trecho do título do episódio: ");
        var trechoTitulo = leitura.nextLine();

        episodioList.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst()
                .ifPresent(System.out::println);

        System.out.println("A partir de que ano você deseja ver os episódios? ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        episodioList.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                ", Episódio: " + e.getTitulo() +
                                ", Data lançamento: " + e.getDataLancamento().format(formatador)
                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
