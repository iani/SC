/* IZ Tuesday, May 20, 2008 
Enable consistent loading and reloading and allocation of buffers. Make buffers available to scripts, ensuring that the buffers will be loaded when the server boots and that any dependants such as scripts or other objects will be notified when a buffer receives its bufnum at allocation time.

This class was started to clear up problems with buffer allocation at startup that were encountered with Samples, by refactoring all buffer loading methods out of Samples and of Script. 

As of 080619 no progress is made, so I should take up this again some time later. 

*/

BufferLoader {
	
}
