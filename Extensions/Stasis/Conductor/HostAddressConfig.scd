/* IZ May 24, 2010
global config of piece


IP addresses: 

iani 192.168.1.10
graphics 192.168.1.11
manolis 192.168.1.12
aris 192.168.1.13
dakis 192.168.1.14
omer 192.168.1.15

*/


(
~everybody = [\iani, \graphics, \manolis, \aris, \dakis, \omer];

~everybody do: { | n, i |
	currentEnvironment.put(n, NetAddr(format("192.168.1.%", 10 + i), 57120))
};
)

(
r = { 
	loop {
		~everybody do: { | name | currentEnvironment[name].postln.sendMsg('test') };
		0.5.wait;
	}

}.fork
)

NetAddr.localAddr

~iani;

currenEnvironment