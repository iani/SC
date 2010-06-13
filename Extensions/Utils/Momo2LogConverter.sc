/* IZ June 12, 2010

Convert files exported from http://www.momolog.com/app to format used for Log.org files in org mode for Emacs agenda. 
This class is for personal use but could be adapted for similar use elsewhere. 

Momo2Log.convert;

Momo2Log.new.readMomoEntries;

*/


Momo2Log {
	var logFileDirectory = "/Users/iani/Documents/Notes/Org/subfiles/";
	var basename = "Log";
	var extension = ".org";
	var momopath = "/Users/iani/Downloads/momo.csv";
	var entries;

	*convert {
		this.new.convert;
	}

	convert {
		this.readLogEntries;
		this.makeBackup;
		this.readMomoEntries;
		this.sortEntries;
		this.writeLogEntries;
	}
	
	readLogEntries {
		File.use(this.logPath, "rt", { | file |
			var log, positions, startpos, endpos, date, length;
			log = file.readAllString;
			length = log.size;
			positions = log.findRegexp("\\* <\\d+-\\d{2}-\\d{2}[^>]*>");
			entries = positions collect: { | p, i |
				#startpos, date = p;
				endpos = positions[i + 1];
				if (endpos.isNil) { 
					endpos = length;
				}{
					endpos = endpos[0];
				};
				log.copyRange(startpos, endpos - 1);
			}
		});
	}
	
	logPath {
		^logFileDirectory ++ basename ++ extension;
	}
	
	readMomoEntries {
		var m_entries;
		File.use(momopath, "rt", { | file |
			var hour, date, char, entry;
			entry = "".copy;
			// Read file bytewise. Check for unprintable bytes. Create entries
			while { (char = file.getChar).notNil } {
				if (char.ascii == -1) { // -1 signals start of entry in CSV format
					m_entries = m_entries add: entry;
					entry = "".copy;
				}{	// drop unprintable characters and linefeeds
					if (char.ascii > 0 and: { char.ascii != 13}) {
						if (char.ascii != 9) {
							entry = entry add: char;
						}{ 	// convert tabs to " - "
							entry = entry ++ " - ";
						}
					}
				}
			};
			// Add the last entry without expecting next entry char:
			m_entries = m_entries add: entry;
			m_entries = m_entries.select { | e | e.size > 10 } collect: { | e |
				date = e.findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^.+]*").first[1];
				// could 'date' already found be used for the replace below?
				e = e.replace(e.findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[.]*\\d*\\+00:00").first[1], "");
				// add 3 hours to 
				hour = (date[11..12].interpret + 103).asString[1..];
				date[11] = hour[0];
				date[12] = hour[1];
				format("* <%> %", date, e);
			};
		});
		// Add momo entries to log entries
		"------- Adding new entries from momo to Log entries: ------- ".postln;
		m_entries do: { | m, i |
			postf("%, ", i);
			// Only add those entries that do not already exist
			if (entries.detect({ | e | e == m }).isNil) {
				postf("\n Adding:\n%\n", m);
				entries = entries add: m
			}
		};
	}
	
	makeBackup {
		format("cd %\nmv %% %_backup_%%", logFileDirectory, basename, extension,
			basename, Date.localtime.stamp, extension).unixCmd;
	}
	
	sortEntries {
		entries = entries.sort;
	}

	writeLogEntries {
		// first write to Log.orgxxx.org, then rename. Writing straight to Log.org does not work
		File.use(this.logPath ++ "xxx.org", "wt", { | file |
			entries do: file.putString(_);
		});
		// Rename back to Log.org
		format("mv % %", this.logPath ++ "xxx.org", this.logPath).unixCmd;
	}

}
