#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>

int main() {
  int sd;
  struct sockaddr_in dest;
  char msg[] = "Hello, World!";

  dest.sin_family = AF_INET;
  if (inet_pton(AF_INET, "127.0.0.1", &(dest.sin_addr)) != 1) {
      printf("Bad Address!\n");
      return(1);
  }

  if ((sd = socket(AF_INET, SOCK_RAW, 253)) < 0) {
      printf("socket() failed!\n");
      return(1);
  }

  if (sendto(sd, &msg, 14, 0, (struct sockaddr*) &dest, sizeof(struct sockaddr)) < 0)  {
    printf("sendto() failed!\n");
    return(1);
  }

  return(0);
}
