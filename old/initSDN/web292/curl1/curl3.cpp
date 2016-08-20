#include <iostream>
#include <sys/socket.h>
#include <resolv.h>
#include <arpa/inet.h>
#include <unistd.h>

using namespace std;

int main()
{
  int s, error;
  struct sockaddr_in addr;

  if((s = socket(AF_INET,SOCK_STREAM,0))<0)
    {
      cout<<"Error 01: creating socket failed!\n";
      close(s);
      return 1;
    }

  addr.sin_family = AF_INET;
  addr.sin_port = htons(80);
  inet_aton("204.27.61.92",&addr.sin_addr);

  error = connect(s,(sockaddr*)&addr,sizeof(addr));
  if(error!=0)
    {
      cout<<"Error 02: conecting to server failed!\n";
      close(s);
      return 1;
    }

  char msg[] = "GET /beej/inet_ntoaman.html http/1.1\nHOST: retran.com\n\n";

  // char msg[] = "GET  HTTP/1.1";
  char answ[1024];
  //cin.getline(&msg[0],256);

  send(s,msg,sizeof(msg),0);

  // while(recv(s,answ,1024,0) > 0)
  //   cout<<answ<<endl;

  ssize_t len;
  while((len = recv(s, answ, 1024, 0)) > 0)
    std::cout.write(answ, len);
  std::cout << std::flush;


  close(s);
  cin.getline(&msg[0],1);
  return 0;
}
