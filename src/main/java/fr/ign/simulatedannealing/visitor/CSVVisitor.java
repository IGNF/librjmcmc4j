package fr.ign.simulatedannealing.visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import fr.ign.mpp.configuration.ListConfiguration;
import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class CSVVisitor<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Visitor<C, M> {
	BufferedWriter writer;
	int iter;
	int dump;
	int save;
	private String textSeparator = " ";

	public CSVVisitor(String fileName) {
		Path path = Paths.get(fileName);
		try {
			writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(int dump, int save) {
		this.iter = 0;
		this.dump = dump;
		this.save = save;
	}

	@Override
	public void begin(C config, Sampler<C, M> sampler, Temperature t) {
		// String s = "Iteration" + textSeparator + "NB" + textSeparator +
		// "Temp" + textSeparator
		// + "ENERGIE";
		// try {
		// this.writer.append(s);
		// this.writer.newLine();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void visit(C config, Sampler<C, M> sampler, Temperature t) {
		if (t == null)
			return;
		if (this.iter % this.dump == 0) {
			String s = this.iter + textSeparator;
			if (config instanceof ListConfiguration<?, ?, ?>) {
				s += ((ListConfiguration<?, ?, ?>) config).size() + textSeparator;
			}
			s += t.getTemperature(0) + textSeparator + config.getEnergy();
			try {
				this.writer.append(s);
				this.writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.iter % this.save == 0) {
			try {
				this.writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.iter++;
	}

	@Override
	public void end(C config, Sampler<C, M> sampler, Temperature t) {
		try {
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
