package br.com.alura.screenmatch;

import br.com.alura.screenmatch.domain.SerieDTO;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String endereco = "https://www.omdbapi.com/?t=gilmore+girls&apikey=402e2999";
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados(endereco);
//		System.out.println(json);
//		json = consumoApi.obterDados("https://coffee.alexflipnote.dev/random.json");
		System.out.println(json);
		ConverteDados conversor = new ConverteDados(new ObjectMapper());
		SerieDTO serieDTO = conversor.obterDados(json, SerieDTO.class);
		System.out.println(serieDTO);
	}
}
