package org.jdna.newreleases;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by seans on 12/08/16.
 */
public class DvdReleaseDatesFactory extends Converter.Factory {
    public static Converter.Factory create() {
        return new DvdReleaseDatesFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new MoviesResponseBodyConverter();
    }
}
