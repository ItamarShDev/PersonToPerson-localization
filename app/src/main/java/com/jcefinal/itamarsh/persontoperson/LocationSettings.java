package com.jcefinal.itamarsh.persontoperson;
/**
 * Created by itamar on 14-Dec-15.
 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/* This class treat's Location Permissions*/
public class LocationSettings {

        private Activity mActivity;
        private final int PERMISSION_REQUEST = 0;
        private OnPermissionListener mOnPermissionListener;

        public interface OnPermissionListener {
            void OnPermissionChanged(boolean permissionGranted);
        }

        public LocationSettings(Activity activity, OnPermissionListener onPermissionListener) {
            mActivity = activity;
            setOnPermissionListener(onPermissionListener);
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Permission is already available
                if (mOnPermissionListener != null) {
                    mOnPermissionListener.OnPermissionChanged(true);
                }
            } else {
                // Permission is missing and must be requested.
                requestPermission();
            }
        }

        private void requestPermission() {

            // Permission has not been granted and must be requested.
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(mActivity,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);

            } else {

                if (mOnPermissionListener != null) {
                    mOnPermissionListener.OnPermissionChanged(false);
                }

                // Request the permission. The result will be received in onRequestPermissionResult().
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            }
        }

        public void setOnPermissionListener(OnPermissionListener onPermissionListener){
            mOnPermissionListener = onPermissionListener;
        }


        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == PERMISSION_REQUEST) {
                // Request for permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission granted
                    if (mOnPermissionListener != null) {
                        mOnPermissionListener.OnPermissionChanged(true);
                    }

                } else {
                    //permission denied
                    if (mOnPermissionListener != null) {
                        mOnPermissionListener.OnPermissionChanged(false);
                    }
                }
            }
        }
    }

