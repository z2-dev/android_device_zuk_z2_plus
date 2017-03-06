#define LOG_TAG "zuk_readmac"
#define LOG_NDEBUG 0

#include <cutils/log.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>

#define MAC_ADDR_SIZE 6
#define WLAN_MAC_BIN "/persist/wlan_mac.bin"

static int check_wlan_mac_bin_file()
{
    char content[1024];
    FILE *fp;

    fp = fopen(WLAN_MAC_BIN, "r");
    if (fp != NULL) {
        memset(content, 0, sizeof(content));
        fread(content, 1, sizeof(content)-1, fp);
        fclose(fp);

        if (strstr(content, "Intf0MacAddress") == NULL) {
            ALOGV("%s is missing Intf0MacAddress entry value", WLAN_MAC_BIN);
            return 1;
        }

        if (strstr(content, "Intf1MacAddress") == NULL) {
            ALOGV("%s is missing Intf1MacAddress entry value", WLAN_MAC_BIN);
            return 1;
        }

        return 0;
    }
    return 1;
}

int main()
{
    int i, wlan_addr3, wlan_addr4, wlan_addr5, ret;

    // First 6 hex number are fix 
    unsigned char wlan_addr[] = { 0xd8, 0x9a, 0x34 };

    // Last 6 hex number are random
    srand(time(NULL) + getpid());
    for (i = 0; i < 3; i++) {
    	wlan_addr3 = rand() % 256;
    	wlan_addr4 = rand() % 256;
    	wlan_addr5 = rand() % 256; 
    	}

    FILE *fp;

    // Store WLAN MAC address in the persist file, if needed
    if (check_wlan_mac_bin_file()) {
        fp = fopen(WLAN_MAC_BIN, "w");
        fprintf(fp, "Intf0MacAddress=%02X%02X%02X%02X%02X%02X\n",
                wlan_addr[0], wlan_addr[1], wlan_addr[2], wlan_addr3, wlan_addr4, wlan_addr5);
        fprintf(fp, "Intf1MacAddress=%02X%02X%02X%02X%02X%02X\n",
                wlan_addr[0], wlan_addr[1], wlan_addr[2], wlan_addr3, wlan_addr4, (wlan_addr5+1));
        fprintf(fp, "END\n");
        fclose(fp);
        ALOGV("%s was successfully generated", WLAN_MAC_BIN);
    } else {
        ALOGV("%s already exists and is valid", WLAN_MAC_BIN);
    }

    return 0;
}
