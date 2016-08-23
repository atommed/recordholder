#include <stdlib.h>
#include <stdio.h>

#include <libavformat/avformat.h>
#include <libavutil/dict.h>

int main(int argc, char **argv){
	AVFormatContext *fmt_ctx = NULL;
	AVDictionaryEntry *tag = NULL;
	int ret;

	if(argc != 2){
		printf("Usage: %s <input file>\n"
		       "Test ffmpeg\n"
		       "\n", argv[0]);
		exit(EXIT_FAILURE);
	}      

	av_register_all();
	if((ret = avformat_open_input(&fmt_ctx, argv[1], NULL, NULL)))
		exit(ret);

	while((tag = av_dict_get(fmt_ctx->metadata, "", tag, AV_DICT_IGNORE_SUFFIX)))
		printf("%s=%s\n", tag->key, tag->value);
	
	avformat_close_input(&fmt_ctx);
	exit(EXIT_SUCCESS);
}
