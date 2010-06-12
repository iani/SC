/* IZ June 10, 2010
Parsing org mode type log files and merging with exports from http://www.momolog.com/app
*/

/* 1: Creating a backup of the old log file
then sorting its contents and rewriting them.
We store the entries in a list sorted by date. This list can be used to insert the entries imported from momolog.com. 
We need to check for duplicates though. 
*/

(
f = Event make: {
	var endpos, entry_positions, entries, log;
	~logFileDirectory = "/Users/iani/Documents/Notes/Org/subfiles/";
	~basename = "Log";
	~extension = ".org";
	~logfilepath = ~logFileDirectory ++ ~basename ++ ~extension;
	~logfile = File(~logfilepath, "rt");
	~log = log = ~logfile.readAllString;
	// rename Log file to backup with timestamp in order to write new Log file in its place. 
	format("cd %\nmv %% %_backup_%%", ~logFileDirectory, ~basename, ~extension,
		~basename, Date.localtime.stamp, ~extension).unixCmd;
	~entry_positions = entry_positions = log.findAll("\n**** "); 
	entries = Array newClear: entry_positions.size;
	entry_positions do: { | p, i |
		endpos = entry_positions[i + 1];
		if (endpos.isNil) {
			endpos = log.size;
		}{
			endpos = endpos - 1;
		};
		entries[i] = log.copyRange(p, endpos);
	};
	~sorted_entries = entries.collect { | e |
		[e.findRegexp("<....-..-..*>").first[1], e]
	}.sort { | a, b | a[0] < b[0] }; 
	~entries = entries;
	// we have to write on a different name and then rename, because for some reason
	// the shell vs. SC file access coordination does not permit writing Log.org right away.
	~sortedEntryFile = File(format("%LogX.org", ~logFileDirectory), "wt");
	~sorted_entries.postln do: { | e |
		~sortedEntryFile.putString(e[1]);
	};
	~sortedEntryFile.close;
	// so finally, rename the file back to Log.org
	format("cd %\nmv % %", ~logFileDirectory, "LogX.org", "Log.org").unixCmd;
};
)

f.sorted_entries.first.first
f.sorted_entries.first[1];
g.entries.first[1];

/* 2: Parse the entries exported from http://www.momolog.com/app and convert them to Log.org entries */

File.exists("/Users/iani/Downloads/momo.csv")
f = File("/Users/iani/Downloads/momo.csv", "r");
g = f.readAllString;
g.size;

(
g = Event make: {
	var endpos, entry_positions, entries, log, char, entry, date, hour;
	~logFileDirectory = "/Users/iani/Downloads/";
	~basename = "momo";
	~extension = ".csv";
	~logfilepath = ~logFileDirectory ++ ~basename ++ ~extension;
	~logfilepath.postln;
	~logfile = File("/Users/iani/Downloads/momo.csv", "rt");
	~entries = [];
	entry = "".copy;
	while { (char = ~logfile.getChar).notNil } {
		if (char.ascii == -1) {
			~entries = ~entries add: entry;
			entry = "".copy;
		}{
			if (char.ascii > 0) {
				entry = entry add: char
			}
		}
	};
//	~entries do: { | e | e.findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+.]*").postln; }
	~entries = ~entries.select { | e | e.size > 10 } collect: { | e |
		date = e.findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^.+]*").first[1];
		e = e.replace(e.findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[.]*\\d*\\+00:00").first[1], "[:MOMO:]");
		hour = (date[11..12].interpret + 103).asString[1..];
		date[11] = hour[0];
		date[12] = hour[1];
		[format("<%>", date), format("**** <%> %", date, e)];
	};
};
)

g.entries.flop.first;
g.entries.first[1];
g.entries[1];
g.entries do: _.postln;


/*
(
a = "2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*").first[1];
b = (a[11..12].interpret + 103).asString[1..];
a[11] = b[0];
a[12] = b[1];
)

"2010-06-08 16:40:19.12341234+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[.]*\\d*\\+00:00").first[1];
"2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[.]*\\d*\\+00:00").first[1];

*/



(// === just a test here:

g = Event.make {
	~logFileDirectory = "/Users/iani/Documents/Notes/Org/subfiles/";
	~basename = "Log";
	~extension = ".org";
	format("cd %\nmv %% %_backup%", ~logFileDirectory, ~basename, ~extension, ~basename, ~extension).unixCmd;
/*
	format("cd %\nmv Log.org, Log_backup_.org", 
		~logFileDirectory // ,
//		format("%%%", ~logFileDirectory, ~logFileBaseName, ~logFileNameExtension);
//		format("%%_backup_%%", ~logFileDirectory, ~logFileBaseName, Date.localtime.stamp, ~logFileNameExtension);
	).unixCmd;
*/
};
)