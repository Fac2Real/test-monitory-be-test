   package com.factoreal.backend.Entity;

   import jakarta.persistence.*;
   import lombok.Getter;
   import lombok.Setter;
   import lombok.AllArgsConstructor;
   import lombok.NoArgsConstructor;

   // 센서 정보 Entity
   @Entity
   @Table(name = "sensor")
   @Getter
   @Setter
   @AllArgsConstructor
   @NoArgsConstructor
   public class Sensor {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @Column(name = "sensor_id", unique = true) // 센서ID
       private String sensorId;

       @Column(name = "sensor_type") // 센서종류
       private String sensorType;

       @Column(name = "sensor_status") // 센서상태
       private String sensorStatus;

       @Column(name = "sensor_purpose") // 센서목적
       private String sensorPurpose;

       @Column(name = "location")   // 위치
       private String location;

       @Column(name = "threshold") // 임계치
       private Integer threshold;

       // 기본 생성자, getter/setter, 필요 시 빌더   생성
   }