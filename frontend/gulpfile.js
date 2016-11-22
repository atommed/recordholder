var path = require('path');
var gulp = require('gulp');
var plumber = require('gulp-plumber');
var webpack = require('webpack-stream');
var sass = require('gulp-sass');
var sourcemaps = require('gulp-sourcemaps');

gulp.task('html', function(){
	return gulp.src('app/**/*.html')
	.pipe(gulp.dest('public_html'))
});

gulp.task('scripts', function(){
	return gulp.src('app/js/main.jsx')
	.pipe(plumber())
	.pipe(webpack({
		module: {
			loaders: [{
				test: /\.jsx/,
				loaders: ['babel'],
				include: path.join(__dirname, 'app','js')
			}]
		},
		output: {
			filename: 'bundle.js'
		},
		devtool: 'source-map'
	}))
	.pipe(gulp.dest('public_html/'))
});

gulp.task('sass', function(){
	return gulp.src('app/sass/**/*.scss')
        .pipe(sourcemaps.init())	
	.pipe(sass().on('error', sass.logError))
	.pipe(sourcemaps.write())
	.pipe(gulp.dest('public_html/css'))
});

gulp.task('watch', function(){
	gulp.watch('app/js/**/*.jsx', ['scripts']);
	gulp.watch('app/sass/**/*.scss', ['sass']);
	gulp.watch('app/**/*.html', ['html'])
});

gulp.task('default', ['watch', 'scripts', 'sass','html']);
