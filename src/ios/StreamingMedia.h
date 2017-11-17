#import <Foundation/Foundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import <Cordova/CDVPlugin.h>

@interface StreamingMedia : CDVPlugin

- (void)getVideoProgress:(CDVInvokedUrlCommand*)command;
- (void)playVideo:(CDVInvokedUrlCommand*)command;
- (void)playAudio:(CDVInvokedUrlCommand*)command;

@end