/* IZ reading form data from server in quasi JSON format 

Very rudimentary first steps. 

// FormParser.read; // NOT THIS ONE

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! DO THIS ONE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
FormParser.readAndProcess;

// Following are tries only 
FormParser.makeValuePairs;

FormParser.makeStatistics;

FormParser.statistics.keys;

a = FormParser.all.asArray.first;

a.replace("a:", "a_")

a.inspect;


FormParser.all

======================

*/

FormParser {
	classvar <>all;
	classvar <>statistics;
	
	*initClass {
		all = IdentityDictionary.new;
		statistics = IdentityDictionary.new;
	}
	
	*read {
		Dialog.getPaths({ | paths |
			paths do: { | path |
				all[PathName(path).fileNameWithoutExtension.asSymbol] = 
				File(path.postln, "r").readAllString.interpret;
			}
		});
	}
	
	*makeValuePairs { ^all.values.asArray collect: _.clump(2); }
	
	*makeStatistics {
		var records, recordLists, recordLists2, groupedItems;
		records = this.makeValuePairs;
		records do: { | record | 
			record do: { | keyval |
				statistics[keyval[0].asSymbol] = statistics[keyval[0].asSymbol] add: keyval[1];
			}
		};
		recordLists = List.new;
		statistics keysValuesDo: { | key, value | recordLists add: NamedList(key.postln, value); };
		recordLists = recordLists.sort({ | a, b | a.name < b.name });
		AppModel().window({ | window, app |
			window.layout = VLayout(
				app.listView(\keys, { | me | me.value.adapter.items collect: _.name })
					.items_(recordLists).view,
				app.listView(\details).sublistOf(\keys).view
			)
		});
		recordLists2 = recordLists collect: { | nl |
			groupedItems = IdentityDictionary.new;
			nl.array do: { | i | 
				groupedItems[i.asSymbol] = groupedItems[i.asSymbol] add: i
			};
			groupedItems = groupedItems.keys.asArray.sort collect: { | k |
				[k, groupedItems[k].size]
			};
			NamedList(nl.name, groupedItems); 
		};
		AppModel().window({ | window, app |
			window.layout = VLayout(
				app.listView(\keys, { | me | me.value.adapter.items collect: _.name })
					.items_(recordLists2).view,
				app.listView(\details, { | me |
					me.value.adapter.items collect: { | i | format("% : %", i[0], i[1]) };
				}).sublistOf(\keys).view
			)
		});		
	}
	
	*readAndProcess {
		Dialog.getPaths({ | paths |
			paths do: { | path |
				all[PathName(path).fileNameWithoutExtension.asSymbol] = 
				File(path.postln, "r").readAllString.interpret;
			};
			this.makeStatistics;
			this.saveStatistics;
		});
	}

	*saveStatistics {
		var file;
		file = File.open(Platform.userAppSupportDir +/+ "protimiseis_1eton.txt", "w");
		statistics.keys.asArray.sort do: { | key |
			file.putString("\n\n ==================================== ");
			file.putString(key.asString);
			file.putString(" ====================================\n\n");
			file.putString(statistics[key].asCompileString);
		};
		file.close;
	}
}
