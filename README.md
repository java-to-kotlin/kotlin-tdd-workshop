

How to fake serial comms with fake hardware using TCP/IP

1. Run the fake hardware on port 3000 (for example).
2. Run Gradle task installDist to build the control app
3. Run: `3<>/dev/tcp/localhost/3000 ./build/install/bowling/bin/bowling $NUMBER_OF_PLAYERS` 


To fake the hardware with netcat, run: `nc -lk 3000`.  Hardware commands will be written to the terminal, and you can enter hardware events in the terminal. 

When you run the app, you will see the command "RESET". Respond by typing "READY".  Then type "PINFALL $N"  (e.g. "PINFALL 1" for one pin down, "PINFALL 10" for a strike, etc.).  You will then see a hardware command "SET PARTIAL" or "SET FULL".  Respond with another PINFALL event.
