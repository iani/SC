/* IZ 120309 
Open help old style on a folder
*/

+ Help { *dialog { Dialog.openPanel({ | paths | HelpQtCompatible(paths.first).gui }) } }

