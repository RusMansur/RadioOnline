package application;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class RadioStationDB {
	static File radioListFile = new File("radiolist.csv").getAbsoluteFile();

	TreeMap<String, RadioStation> radioStationDB = new TreeMap<>();

	public void createStationsListFile() {
		if (!radioListFile.exists()) {
			try {
				radioListFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	TreeMap<String, RadioStation> readStationsFromFile() {
		try (CSVReader reader = new CSVReader(new FileReader(radioListFile))) {
			String[] field;
			while (true) {
				if ((field = reader.readNext()) != null) {
					RadioStation radioStation = new RadioStation(
							field[0],
							field[1],
							field[2]
					);
					radioStationDB.put(radioStation.getName(), radioStation);
				} else {break;}
			}
		} catch (IOException | CsvValidationException e) {
			e.printStackTrace();
		}
		return radioStationDB;
	}

	void saveStationToFile(RadioStation radioStation) {
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(radioListFile, true))) {
			String[] station = {
					radioStation.getName(),
					radioStation.getLogoPath(),
					radioStation.getUrl()
			};
			csvWriter.writeNext(station);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	void saveListOfStationsToFile(TreeMap<String, RadioStation> radioStationList) {
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(radioListFile))) {
			for (Map.Entry<String, RadioStation> entry : radioStationList.entrySet()) {
				String[] fields = {
						entry.getKey(),
						entry.getValue().getLogoPath(),
						entry.getValue().getUrl()
				};
				csvWriter.writeNext(fields);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	ObservableList<String> nameList() {
		ObservableList<String> nameList = FXCollections.observableArrayList();
		for (Map.Entry<String, RadioStation> entry : radioStationDB.entrySet()) {
			nameList.add(entry.getKey());
		}
		return nameList;
	}

	RadioStation getRadioStation(String name) {
		for (Map.Entry<String, RadioStation> entry : radioStationDB.entrySet()) {
			if (entry.getKey().equals(name)) return entry.getValue();
		}
		return null;
	}
}
