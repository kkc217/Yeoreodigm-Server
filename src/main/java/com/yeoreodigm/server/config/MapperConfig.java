//package com.yeoreodigm.server.config;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.yeoreodigm.server.dto.member.MemberInfoDto;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//public class MapperConfig {
//
//    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//    @Bean
//    public ObjectMapper serializingObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
////        JavaTimeModule javaTimeModule = new JavaTimeModule();
////        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
////        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
////        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
////        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
//
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(MemberInfoDto.class, new MemberInfoDtoSerializer());
//        simpleModule.addDeserializer(MemberInfoDto.class, new MemberInfoDtoDeSerializer());
//
//        objectMapper.registerModules(simpleModule);
////        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        return objectMapper;
//    }
//
//    public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
//
//        @Override
//        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            gen.writeString(value.format(FORMATTER));
//        }
//    }
//
//    public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//
//        @Override
//        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//            return LocalDateTime.parse(p.getValueAsString(), FORMATTER);
//        }
//    }
//
//    public class LocalDateSerializer extends JsonSerializer<LocalDate> {
//        @Override
//        public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
//            generator.writeString(value.format(DATE_FORMATTER));
//        }
//    }
//
//    public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
//        @Override
//        public LocalDate deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
//            return LocalDate.parse(parser.getValueAsString(), DATE_FORMATTER);
//        }
//    }
//
//    public class MemberInfoDtoSerializer extends JsonSerializer<MemberInfoDto> {
//        @Override
//        public void serialize(MemberInfoDto value, JsonGenerator generator, SerializerProvider provider) throws IOException {
//            generator.writeStartObject();
//
//            generator.writeFieldName("memberId");
//            generator.writeString(String.valueOf(value.getMemberId()));
//
//            generator.writeFieldName("email");
//            generator.writeString(value.getEmail());
//
//            generator.writeFieldName("nickname");
//            generator.writeString(value.getNickname());
//
//            generator.writeFieldName("authority");
//            generator.writeObject(value.getAuthority());
//
//            generator.writeFieldName("surveyIndex");
//            generator.writeString(String.valueOf(value.getSurveyIndex()));
//
//            generator.writeEndObject();
//        }
//    }
//
//    public class MemberInfoDtoDeSerializer extends JsonDeserializer<MemberInfoDto> {
//        @Override
//        public MemberInfoDto deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
////            parser.getValueAsString("")
////            return LocalDate.parse(parser.getValueAsString(), DATE_FORMATTER);
//            System.out.println("hihihhihihihhihi");
//            System.out.println(parser.getValueAsString("authority"));
//            return null;
//        }
//    }
//
//}
