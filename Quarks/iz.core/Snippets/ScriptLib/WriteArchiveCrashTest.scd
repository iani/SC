
(
f = {
	({ format("%: ", Date.getDate, "asdf".scramble) } ! 1000)
	.writeArchive(Platform.userAppSupportDir +/+ "shutdowntest.stxar");
};


ShutDown add: f;
)

